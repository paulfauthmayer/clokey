(ns clokey.user
  (:require [clojure.string :as string]
            [clojure.java.io :as io]
            [cheshire.core :refer :all]
            [clokey.password :as password]
            [clokey.utils :as utils]
            [monger.core :as mg]
            [monger.collection :as mc])
  (:import  [com.mongodb MongoOptions ServerAddress]
           org.bson.types.ObjectId))


 ;; initialize mongodb

(let [conn (mg/connect)
      db   (mg/get-db conn "clokey-db")]


; <editor-fold> --------USER CRUD -----------

;; CRUD - USERS

  (defn save-user
    "Writes a user 'object' to the Database"
    [user]
    (mc/save-and-return db "users" user))

  (defn create-user [username email mpw]
    (let [new-user
          {:name username
           :mpw mpw
           :email email
           :entries
           []}]
      (save-user new-user)
      new-user))

  (defn get-user
    "Reads a user from Database by username and provides data"
    [username]
    (mc/find-one-as-map db "users" {:name username}))

  (defn delete-user
    "Delete a user"
    [username]
    (mc/remove-by-id db "users" (:_id (get-user username))))

  (defn combine-userdata
    "Helper function ..."
    [old-data new-data]
    {:name (or (:name new-data) (:name old-data))
     :mpw (or (:mpw new-data) (:mpw old-data))
     :email (or (:email new-data) (:email old-data))
     :entries (or (:entries old-data) [])})

  (defn update-user
    "Update userdata by a given input username"
    [username userdata]
    (let [user (get-user username)]
      (mc/update-by-id  db
                        "users"
                        (:_id user)
                        (combine-userdata user userdata))))



  ; </editor-fold>

  ; <editor-fold> --------ENTRY CRUD -----------

  (defn create-entry
    "creates entry if requirements are met, does other stuff otherwise" ;TODO: REDACT!
    ([source username pw]
     (if (password/valid? pw)
      (do
        {:source source
         :username username
         :password  pw})))
    ([source username]
     {:source source
      :username username
      :password (apply password/generate-password '())}))

  (defn set-entry
    ""
    [username entry]
    (let [user (get-user username)]
      (println user)
      (mc/update-by-id
                       db
                       "users"
                       (:_id user)
                       {:name (:name user)
                        :mpw (:mpw user)
                        :email (:email user)
                        :entries (conj (:entries user) entry)})))

  (defn get-entry
    ""
    [username source-name]
    (let [user (get-user username)]
      (let [entries (user :entries)]
       (filter
        #(re-matches (re-pattern source-name) (:source %))
        entries))))

  (defn combine-entrydata
    ;TODO: recursively
    "Helper function ..."
    [old-data new-data]
    {:source (or (:source new-data) (:source old-data))
     :username (or (:username new-data) (:username old-data))
     :password (or (:password new-data) (:password old-data))})

  (defn update-entry
    "Updates a given entry"
    [username source-name new-data]
    (let [user (get-user username)
          entry-split (group-by
                        #(re-matches (re-pattern source-name) (:source %))
                        (:entries user))]
         (->> (combine-entrydata
               (get entry-split source-name)
               new-data)
              (into (get entry-split nil) ,,,)
              (assoc user :entries ,,,)
              (mc/update-by-id
               db
               "users"
               (:_id user)
               ,,,))))

  (defn delete-entry [username source-name]
    (let [user (get-user username)]
      (->> (user :entries)
           (filter
             #(not (re-matches (re-pattern source-name) (:source %)))
             ,,,)
           (assoc user :entries ,,,)
           (mc/update-by-id
            db
            "users"
            (:_id user)
            ,,,))))

  ; </editor-fold>

  ; <editor-fold> -------- AUTHENTICATION -----------

  ;; AUTHENTICATION

  (def authdata)
  "Global var that stores valid users with their
   respective passwords."
  {:admin "secret"
   :test "secret"}

  (defn login-authenticate
    "Check request username and password against authdata
    username and passwords.
    On successful authentication, set appropriate user
    into the session and redirect to the value of
    (:next (:query-params request)). On failed
    authentication, renders the login page."
    [request]
    (let [username (get-in request [:form-params "username"])
          password (get-in request [:form-params "password"])
          session (:session request)
          found-password (:mpw (get-user username))])))
      ; (if (and found-password (= found-password password))
      ;   (let [next-url (get-in request [:query-params :next] "/")
      ;         updated-session (assoc session :identity (keyword username))]
      ;     (-> (redirect next-url)
      ;         (assoc :session updated-session)))
      ;   (let [content (slurp (io/resource "login.html"))]
      ;     (render content request))))))

  ; </editor-fold>
