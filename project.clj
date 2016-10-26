(defproject custom "0.0.1"
  :description "Cryptor"
  :url "http://navigator-hc.ru"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                [ring "1.5.0"]
                [ring/ring-defaults "0.1.5"]
                [compojure "1.5.1"]
                [org.clojure/java.jdbc "0.4.2"]
                [mysql/mysql-connector-java "5.1.6"]
                [clj-jade "0.1.7"]
                [hiccup "1.0.5"]]
  :plugins [[lein-ring "0.9.7"]]
  :ring {
        :handler custom.core/app
        :open-browser? false
  }
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})