(ns clokey.user
  (:require [crypto.password.bcrypt :as password]
            [clojure.string :as string]))

;; EXAMPLE USERS

(def example-user
  {:name "Alex da G"
   :mpw "encrypted-pw"
   :entries
   [{:source "facebook.com"
     :username "alexdag"
     :pw "encrypted-pw"}
    {:source "youtube.com"
      :username "alexdag"
      :pw "encrypted-pw"}
    {:source "myspace.com"
      :username "alexdag"
      :pw "encrypted-pw"}]})

;; CRUD

(defn create-user [username mpw]
  (let [new-user
        {:name username
         :mpw mpw
         :entries
         []}]
    (save-user new-user)
    new-user))

(defn get-user []
  (* 1 1))

(defn delete-user []
  (* 1 1))

(defn update-user []
  (* 1 1))

(defn save-user [user]
  (println "In the future, I'll save this user to the database!"))

;; MANAGE ENTRIES

(defn get-entry [user source-name]
  (let [entries (user :entries)]
    (filter
     #(re-matches (re-pattern source-name) (:source %))
     entries)))

(defn set-entry [user entry]
  (let [new-user
        {:name (:name user)
         :mpw (:mpw user)
         :entries (conj (:entries user) entry)}]
    (println "User before: \n" user)
    (conj (:entries user) entry)
    (println "User after: \n" new-user)))

;; AUTHENTICATION

(defn authenticate []
  (* 1 1))

(defn authenticated? []
  (* 1 1))

;; FROM PASS!

;; CRUD

(defn create-entry
  "creates entry"
  ([source username pw]
   {:source source
    :username username
    :password pw})
  ([source username]
   {:source source
    :username username
    :password generate-password}))

(defn read-entry []
  (* 1 1))

(defn update-entry []
  (* 1 1))

(defn delete-entry []
  (* 1 1))

;; VALIDATION

(defn valid? [pw]
  "Checks whether a password is sufficiently secure. The password must include:
   - minimum length of 10 characters
   - at least one lower case letter
   - at least one upper case letter
   - at least one digit
   - at least one special character: - @ # $ % ^ & + ="
  (let [pattern
        #"^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[-@#$%^&+=_])(?=\S+$).{10,}$"]
    (boolean (re-matches pattern pw))))

;; ENCRYPTION

(defn encrypt [pw]
  pw)

(defn decrypt []
  (* 1 1))

;; GENERATE PASSWORD

(defn generate-password
  ([] (generate-password 10))
  ([length]
   ; Note: the range refers to the numbers assigned to chars in the ASCII charset
   ; for reference: http://www.asciitable.com
   (let [valid-chars (map char (range 33 127))] ; TODO: select proper range
     (apply str (take length (repeatedly #(rand-nth valid-chars)))))))

(def valid-chars (map char (range 33 127)))
