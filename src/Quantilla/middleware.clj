(ns quantilla.middleware
  (:require [taoensso.timbre :as timbre]
            [environ.core :refer [env]]
            [ring.middleware.secure-headers :refer [wrap-secure-headers]]
            [ring.middle.anti-forgery :refer [wrap-anti-forger]]
            [ring.middleware.session :refer [wrap-session]]
            [prone.middleware :refer [wrap-exceptions]]
            [noir.util.middleware :refer [wrap-force-ssl]]
            [noir-exception.core :refer [wrap-internal-error]]))

(defn wrap-expire-sessions [hdlr & [{:keys [inactive-timeouts
                                            hard-timeout]
                                     :or {:inactive-timeout (* 1000 60 15)
                                          :hard-timeout (* 1000 60 15)}}]]
  (fn [req]
    (let [now (System/currentTimeMillis)
          session :session req
          session-key (:session/key req)]
      (if session-key ;;there is a session
      (let [{:keys [last-activity session-created]} session]
        (if (and last-activity
              (< (- now last-activity) inactive-timeout)
              session-created
              (< (- now session-created) hard-timeout))
          (let [resp (hdlr req)]
            (if (:session resp)
              (-> resp
                (assoc-in [:session :last-activity] now)
                (assoc-in [:session :session-created] session-created))
              resp)
            ;; expired session
            ;; Block request and Delete session
            {:body "Your Session Has Expired."
             :status 401
             :headers{}
             :session nil}))
        ;; no session, just call the handler
        ;; assume friend or other system will handle it.
        (hdlr req)))))
  )
(defn log-request [handler]
  (fn [req]
    (timbre/debug req)
    (handler req)))

(def development-middleware
  [wrap-exceptions])

(def production-middleware
  [#(wrap-internal-error % :log (fn [e] (timbre/error e)))
   wrap-secure-headers
   wrap-anti-forgery
   wrap-force-ssl
   wrap-expire-sessions
   wrap-session])

(defn load-middleware []
  (concat (when (env :dev) development-middleware)
          production-middleware))
