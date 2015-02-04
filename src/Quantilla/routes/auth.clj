(ns quantilla.routes.auth
  (:use compojure.core)
  (:require [quantilla.layout :as layout]
            [noir.session :as session]
            [noir.response :as resp]
            [noir.validation :as vali]
            [noir.util.crypt :as crypt]
            [crypto.random :refer [base64]]
            [quantilla.db.core :as db]))

(def smif (str "calKaufG3"))

(defn valid? [id pass1 pass2 secret]
  (vali/rule (vali/has-value? id)
             [:id "User ID is required"])
  (vali/rule (not (db/exists-user id))
             [:id "Duplicated user ID"])
  (vali/rule (vali/min-length? pass 8)
             [:pass1 "Password must be at least 8 characters"])
  (vali/rule (= pass1 pass2)
             [:pass2 "Entered passwords do not match"])
  (vali/rule (= secret smif)
             [:secret "Key is not correct"])
  (not (vali/errors? :id :pass1 :pass2 :secret)))

(defn register [& [id]]
  (layout/render-register
    {:id id
     :id-error (vali/on-error :id first)
     :pass1-error (vali/on-error :pass1 first)
     :pass2-error (vali/on-error :pass2 first)
     :secret-error (vali/on-error :secret first)}))

(defn handle-registration [{:keys [id name pass1 pass2 email secret]}]
  (if (valid? id pass1 pass2 secret)
    (try
      (do
        (db/create-user {:name name :id id :pass (crypt/encrypt (base64 20) pass1) :email email})
        (session/put! :user-id id)
        (resp/redirect "/dashboard"))
      (catch Exception ex
        (vali/rule false [:id (.getMessage ex)])
        (register)))
    (register id)))

(defn profile []
  (layout/render-profile
    (db/get-user (session/get :user-id))))

(defn update-profile [{:keys [name email pass]}]
  (db/update-user (session/get :user-id) name email pass)
  (profile))

(defn handle-login [user pass]
  (let [{id :id :as userData} (db/get-user user)]
    (if (and userData (crypt/compare pass (:pass id)))
      (do (session/put! :user user)
        (resp/redirect "/dashboard"))
      (layout/render-login {:message "Login Information Incorrect"}))))

(defn logout []
  (session/clear!)
  (resp/redirect "/"))

;; Set of routes for authentication.
(defroutes auth-routes
  (GET "/register" []
       (register))

  (POST "/register" {params :params}
        (handle-registration params))

  (GET "/profile" [] (profile))
  
  (POST "/update-profile" {params :params} (update-profile params))
  
  (POST "/login" [user pass]
        (handle-login user pass))

  (GET "/logout" []
        (logout)))
