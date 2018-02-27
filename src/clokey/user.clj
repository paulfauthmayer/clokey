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
    (mc/insert-and-return db "users" user))

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

  (defn update-user
    "Update userdata by a given input username"
    [username userdata]
    (let [user (get-user username)]
      (mc/update-by-id  db
                        "users"
                        (:_id user)
                        (combine-userdata user userdata))))

  (defn combine-userdata
    "Helper function ..."
    [old-data new-data]
    (let [new-user {
                    :name (or (:name new-data) (:name old-data))
                    :mpw (or (:mpw new-data) (:mpw old-data))
                    :email (or (:email new-data) (:email old-data))
                    :entries (or (:entries old-data) [])}]))

  ; </editor-fold>

  ; <editor-fold> --------ENTRY CRUD -----------

  ; //TODO: Verfiy that apply generate-password '() is the right way....
  (defn create-entry
    "creates entry if requirements are met, does other stuff otherwise" ;TODO: REDACT!
    ([source username pw]
     (if (password/valid? pw)
      (do
        (println "success!")
        {:source source
         :username (utils/encrypt username)
         :password (utils/encrypt pw)})
      (println "fail!")))
    ([source username]
     {:source source
      :username (utils/encrypt username)
      :password (utils/encrypt (apply password/generate-password '()))}))


  (defn read-entry []
    (* 1 1))

  (defn update-entry []
    (* 1 1))

  (defn delete-entry [user source-name]
    (let [new-user {
                    :name (user :name)
                    :mpw (user :mpw)
                    :email (user :email)
                    :entries (remove
                              (fn [entry]
                                (= (entry :source) source-name))
                              (user :entries))}]
      (save-user new-user)))



  ;; MANAGE ENTRIES

  (defn get-entry
    ""
    [user source-name]
    (let [entries (user :entries)]
      (filter
       #(re-matches (re-pattern source-name) (:source %))
       entries)))

  (defn set-entry
    ""
    [user entry]
    ; TODO: use assoc instead!
    (let [new-user
          {:name (:name user)
           :mpw (:mpw user)
           :entries (conj (:entries user) entry)}]
      (println "User before: \n" user)
      (conj (:entries user) entry)
      (save-user new-user)
      (println "User after: \n" new-user)
      new-user))

  ; </editor-fold>

  ; <editor-fold> -------- AUTHENTICATION -----------

  ;; AUTHENTICATION

  (defn authenticate []
    (* 1 1))

  (defn authenticated? []
    (* 1 1)))

  ; </editor-fold>
