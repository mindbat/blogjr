(defproject quick-blog "1.0.0-SNAPSHOT"
  :description "An simple, speedy blogging application."
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [compojure "0.6.4"]
                 [ring/ring-jetty-adapter "0.3.1"]
                 [hiccup "0.3.6"]
                 [org.apache.commons/commons-email "1.2"]
                ]
  :dev-dependencies [[lein-ring "0.4.0"]]
  :ring {:handler quick-blog.core/app}
  :repositories {"central-proxy" 
                 "http://repository.sonatype.org/content/repositories/central/"}
)
