(defproject blogjr "1.0.0-SNAPSHOT"
  :description "A simple, speedy blogging application for clojure."
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [postgresql/postgresql "8.4-702.jdbc4"]
                 [org.clojure/java.jdbc "0.1.1"]
                 [compojure "0.6.4"]
                 [ring/ring-jetty-adapter "0.3.1"]
                 [hiccup "0.3.6"]
                 [org.apache.commons/commons-email "1.2"]
                 [clj-time "0.3.0"]
                ]
  :dev-dependencies [[lein-ring "0.4.0"]]
  :ring {:handler blogjr.core/app}
  :repositories {"central-proxy" 
                 "http://repository.sonatype.org/content/repositories/central/"}
)
