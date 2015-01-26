(defproject
  quantilla
  "0.1.0-SNAPSHOT"
  :description
  "Quant Analytics for Stevens SMIF"
  :url
  "http://quantilla.net"
  :dependencies
  [
    [http-kit "2.1.19"]
    [org.clojure/clojure "1.6.0"]
    [ring-server "0.3.1"]
    [mysql/mysql-connector-java "5.1.6"]
    [com.taoensso/timbre "3.3.1"]
    [korma "0.4.0"]
    [prone "0.8.0"]
    [com.novemberain/monger "2.0.0"]
    [log4j
     "1.2.17"
     :exclusions
     [javax.mail/mail
      javax.jms/jms
      com.sun.jdmk/jmxtools
      com.sun.jmx/jmxri]]
    [enlive "1.1.5"]
    [com.taoensso/tower "3.0.2"]
    [com.taoensso/carmin "2.9.0"]
    [markdown-clj "0.9.58" :exclusions [com.keminglabs/cljx]]
    [im.chit/cronj "1.4.3"]
    [noir-exception "0.2.3"]
    [lib-noir "0.9.5"]
    [environ "1.0.0"]
    [ragtime "0.3.6"]]
  :repl-options
  {:init-ns quantilla.repl}
  :jvm-opts
  ["-server"]
  :plugins
  [[lein-ring "0.9.0"]
   [lein-environ "1.0.0"]
   [lein-ancient "0.5.5"]
   [ragtime/ragtime.lein "0.3.6"]]
  :ring
  {:handler quantilla.handler/app,
   :init quantilla.handler/init,
   :destroy quantilla.handler/destroy,
   :uberwar-name "quantilla.war"}
  :profiles
  {:uberjar {:omit-source true, :env {:production true}, :aot :all},
   :production
   {:ring
    {:open-browser? false, :stacktraces? false, :auto-reload? false}},
   :dev
   {:dependencies
    [[ring-mock "0.1.5"]
     [ring/ring-devel "1.3.2"]
     [pjstadig/humane-test-output "0.6.0"]],
    :injections
    [(require 'pjstadig.humane-test-output)
     (pjstadig.humane-test-output/activate!)],
    :env {:dev true}}}
  :ragtime
  {:migrations ragtime.sql.files/migrations,
   :database
   "jdbc:mysql://localhost:3306/quantilla?user=db_user_name_here&password=db_user_password_here"}
  :uberjar-name
  "quantilla.jar"
  :main
  quantilla.core
  :min-lein-version "2.0.0")