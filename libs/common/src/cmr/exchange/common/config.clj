(ns cmr.exchange.common.config
  (:require
   [cmr.exchange.common.file :as file]))

(def config-file "config/cmr-exchange-common/config.edn")

(defn data
  ([]
    (data config-file))
  ([filename]
    (file/read-edn-resource filename)))
