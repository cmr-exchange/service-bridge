# cmr-service-bridge

*A CMR connector service that provides an inter-service API*

[![Build Status][travis-badge]][travis]
[![Security Scan][security-scan-badge]][travis]
[![Dependencies Status][deps-badge]][travis]

[![Clojars Project][clojars-badge]][clojars]
[![Tag][tag-badge]][tag]

[![Clojure version][clojure-v]](project.clj)

[![][logo]][logo]


#### Contents

* [About](#about-)
* [Dependencies](#dependencies-)
* [Documentation](#documentation-)
* [License](#license-)


## About [&#x219F;](#contents)

The Common Metadata Repository (CMR) is a high-performance, high-quality,
continuously evolving metadata system that catalogs Earth Science data and
associated service metadata records. These metadata records are registered,
modified, discovered, and accessed through programmatic interfaces leveraging
standard protocols and APIs.

The set of APIs provided by CMR Service-Bridge allows client applications to
integrate core CMR metadata more easily with other services like OPeNDAP,
ECHO, ESI/EGI, etc.


## Dependencies [&#x219F;](#contents)

* Java
* `lein`


## Documentation [&#x219F;](#contents)

Documentation for CMR Service-Bridge is availble
[here](https://cmr.sit.earthdata.nasa.gov/opendap/docs). The content there
is broken down by category; some of that is offered below as a convenience:

* [Introduction](https://cmr.sit.earthdata.nasa.gov/opendap/docs/current/reference/0000-intro.html)
* [Configuration](https://cmr.sit.earthdata.nasa.gov/opendap/docs/current/reference/0500-configuration.html)
* [Running the Tests](https://cmr.sit.earthdata.nasa.gov/opendap/docs/current/reference/0750-tests.html)
* [Quick Start](https://cmr.sit.earthdata.nasa.gov/opendap/docs/current/reference/1000-quick-start.html)
* [REST API Usage](https://cmr.sit.earthdata.nasa.gov/opendap/docs/current/rest-api)
* [Source Code API Reference](https://cmr.sit.earthdata.nasa.gov/opendap/docs/current/reference/index.html)
* [Source Code Annotation](https://cmr.sit.earthdata.nasa.gov/opendap/docs/current/marginalia/index.html)


## License [&#x219F;](#contents)

Copyright © 2018 NASA

Distributed under the Apache License, Version 2.0.


<!-- Named page links below: /-->

[logo]: https://avatars2.githubusercontent.com/u/32934967?s=200&v=4
[travis]: https://travis-ci.org/cmr-exchange/cmr-service-bridge
[travis-badge]: https://travis-ci.org/cmr-exchange/cmr-service-bridge.png?branch=master
[deps-badge]: https://img.shields.io/badge/deps%20check-passing-brightgreen.svg
[tag-badge]: https://img.shields.io/github/tag/cmr-exchange/cmr-service-bridge.svg
[tag]: https://github.com/cmr-exchange/cmr-service-bridge/tags
[clojure-v]: https://img.shields.io/badge/clojure-1.9.0-blue.svg
[clojars]: https://clojars.org/gov.nasa.earthdata/cmr-service-bridge
[clojars-badge]: https://img.shields.io/clojars/v/gov.nasa.earthdata/cmr-service-bridge.svg
[security-scan-badge]: https://img.shields.io/badge/nvd%2Fsecurity%20scan-passing-brightgreen.svg
