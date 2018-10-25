(ns cmr.ous.impl.v1
  (:require
    [cmr.exchange.common.results.core :as results]
    [cmr.exchange.common.results.errors :as errors]
    [cmr.exchange.common.util :as util]
    [cmr.metadata.proxy.concepts.collection :as collection]
    [cmr.metadata.proxy.concepts.granule :as granule]
    [cmr.metadata.proxy.results.errors :as metadata-errors]
    [cmr.ous.common :as common]
    [cmr.ous.components.config :as config]
    [cmr.ous.results.errors :as ous-errors]
    [taoensso.timbre :as log]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Defaults   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def defualt-processing-level "3")

(def supported-processing-levels
  #{"3" "4"})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Utility/Support Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn sanitize-processing-level
  [level]
  (if (or (= "NA" level)
          (= "Not Provided" level))
    defualt-processing-level
    level))

(defn extract-processing-level
  [entry]
  (log/trace "Collection entry:" entry)
  (sanitize-processing-level
    (or (:processing_level_id entry)
        (get-in entry [:umm :ProcessingLevel :Id])
        defualt-processing-level)))

(defn apply-level-conditions
  ""
  [coll params]
  (let [level (extract-processing-level coll)]
    (log/info "Got level:" level)
    (if (contains? supported-processing-levels level)
      params
      {:errors [ous-errors/unsupported-processing-level
                (format ous-errors/problem-processing-level
                        level
                        (:id coll))]})))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Stages Overrides for URL Generation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn stage2
  [component coll-promise grans-promise {:keys [endpoint token params]}]
  (log/debug "Starting stage 2 ...")
  (let [granules (granule/extract-metadata grans-promise)
        coll (collection/extract-metadata coll-promise)
        tag-data (get-in coll [:tags (keyword collection/opendap-regex-tag) :data])
        data-files (map granule/extract-datafile-link granules)
        service-ids (collection/extract-service-ids coll)
        params (apply-level-conditions coll params)
        vars (common/apply-bounding-conditions endpoint token coll params)
        errs (apply errors/collect (concat [granules coll vars] data-files))]
    (when errs
      (log/error "Stage 2 errors:" errs))
    (log/trace "data-files:" (vec data-files))
    (log/trace "tag-data:" tag-data)
    (log/trace "service ids:" service-ids)
    (log/debug "Finishing stage 2 ...")
    [params data-files service-ids vars tag-data errs]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   API   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-opendap-urls
  [component user-token raw-params]
  (log/trace "Got params:" raw-params)
  (let [start (util/now)
        search-endpoint (config/get-search-url component)
        ;; Stage 1
        [params bounding-box grans-promise coll-promise s1-errs]
        (common/stage1 component
                       {:endpoint search-endpoint
                        :token user-token
                        :params raw-params})
        ;; Stage 2
        [params data-files service-ids vars tag-data s2-errs]
        (stage2 component
                coll-promise
                grans-promise
                {:endpoint search-endpoint
                 :token user-token
                 :params params})
        ;; Stage 3
        [services bounding-info s3-errs]
        (common/stage3 component
                       service-ids
                       vars
                       bounding-box
                       {:endpoint search-endpoint
                        :token user-token
                        :params params})
        ;; Stage 4
        [query s4-errs]
        (common/stage4 component
                       services
                       bounding-box
                       bounding-info
                       {:endpoint search-endpoint
                        :token user-token
                        :params params})
        ;; Error handling for all stages
        errs (errors/collect
              start params bounding-box grans-promise coll-promise s1-errs
              data-files service-ids vars s2-errs
              services bounding-info s3-errs
              query s4-errs
              {:errors (errors/check
                        [not data-files metadata-errors/empty-gnl-data-files])})]
    (common/process-results {:params params
                             :data-files data-files
                             :tag-data tag-data
                             :query query} start errs)))
