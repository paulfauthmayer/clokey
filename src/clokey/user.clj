(ns clokey.user
  (:require [clojure.string :as string]
            [clojure.java.io :as io]
            [cheshire.core :refer :all]
            [clokey.password :as password]
            [clokey.utils :as utils]
            [monger.core :as mg]
            [monger.collection :as mc]
            [buddy.hashers :as hashers])
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

  (defn create-user
    "Creates a user object and passes the object to save-user to save
     into the db, hashes master password"
    [username email mpw]
    (let [new-user
          {:name username
           :mpw (hashers/derive mpw)
           :email email
           :entries []}]
      (save-user new-user)
      new-user))

  (defn get-user
    "Reads a user from Database by username and provides the user object"
    [username]
    (mc/find-one-as-map db "users" {:name username}))

  (defn delete-user
    "Delete a complete user object"
    [username]
    (mc/remove-by-id db "users" (:_id (get-user username))))

  (defn combine-userdata
    "Helper function combining old existing user data with new input data."
    [old-data new-data]
    {:name (or (:name new-data) (:name old-data))
     :mpw (or (hashers/derive (:mpw new-data)) (:mpw old-data))
     :email (or (:email new-data) (:email old-data))
     :entries (or (:entries old-data) [])})

  (defn update-user
    "Update userdata by a given input username, combines input data with
     existing data"
    [username userdata]
    (let [user (get-user username)]
      (mc/update-by-id  db
                        "users"
                        (:_id user)
                        (combine-userdata user userdata))))



  ; </editor-fold>

  ; <editor-fold> --------ENTRY CRUD -----------

  (defn create-entry
    "Creates an entry object. Password can be either specified or
     auto-generated. Object will not be linked to user by this function"
    ([source username pw]
     (println source username "." pw ".")
     (cond
       (password/valid? pw) (hash-map :source source
                                      :username username
                                      :password pw)
       (utils/nil-or-empty? pw) (hash-map :source source
                                      :username username
                                      :password (apply password/generate-password '()))
       :else nil)))

  (defn set-entry
    "Associates an entry object to a user. Persists the user object to the
     database"
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
    "Reads an entry from the database by given username/entry source-name"
    [username source-name]
    (let [user (get-user username)]
      (let [entries (user :entries)]
       (filter
        #(re-matches (re-pattern source-name) (:source %))
        entries))))

  (defn combine-entrydata-old
    "Helper function combining old existing entry data with new input data."
    [old-data new-data]
    {:source (or (:source new-data) (:source old-data))
     :username (or (:username new-data) (:username old-data))
     :password (or (:password new-data) (:password old-data))})

  (defn combine-entrydata
    "Helper function combining old existing entry data with new input data.
     Does the same as the one above, albeit recursively."
    [old-data new-data]
    (loop [result {}
           ks (keys (first old-data))]
      (if (empty? ks)
        result
        (let [key (first ks)]
          (recur (assoc result key (or (get new-data key) (get old-data key)))
                 (rest ks))))))

  (defn update-entry
    "Reads an entry from database and updates it, persists the user object
     back to the db"
    [username source-name new-data]
    (let [user (get-user username)
          entry-split (group-by
                        #(re-matches (re-pattern source-name) (:source %))
                        (:entries user))]
         (some->>  (combine-entrydata
                    (get entry-split source-name)
                    new-data)
                   (vector ,,,)
                   (into (get entry-split nil) ,,,)
                   (assoc user :entries ,,,)
                   (mc/update-by-id
                    db
                    "users"
                    (:_id user)
                    ,,,))))

  (defn delete-entry
    "Delete an entry by a given source, persists the modified user objects
     back to the db"
    [username source-name]
    (let [user (get-user username)]
      (some->> (user :entries)
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

  (defn remove-id
    "Removes Mongo db specific id from user object, the id should not be
     passed to the frontend"
    [input]
    (dissoc input :_id))

  (defn remove-mpw
    "Removes master password from the user object, master password needs to
     stay secure"
    [input]
    (dissoc input :mpw))

  (defn sort-entries
    "Sorts the entries of a given user alphabetically by sourcename"
    [user]
    (->> (user :entries)
         (sort-by :source ,,,)
         (assoc user :entries ,,,)))

  (defn user-valid?
    "Evaluates whether the user is valid for creation. For this, neither of the
     values may be nil, empty or ''. Also, the username may not yet be present
     in the db."
    [username email mpw]
    (cond
      (utils/nil-or-empty? username) false
      (utils/nil-or-empty? email) false
      (utils/nil-or-empty? mpw) false
      (password/valid? mpw) false
      (get-user username) false
      :else true)))
