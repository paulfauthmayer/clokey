(ns clokey.user
  (:require [crypto.password.bcrypt :as password]
            [clojure.string :as string]
            [clojure.java.io :as io]))

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

;; CRUD - USERS

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

;; AUTHENTICATION

(defn authenticate []
  (* 1 1))

(defn authenticated? []
  (* 1 1))

;; CRUD

(defn create-entry
  "creates entry if requirements are met, does other stuff otherwise" ;TODO: REDACT!
  ([source username pw]
   (if (valid? pw)
    (do
      (println "success!")
      {:source source
       :username (encrypt username)
       :password (encrypt pw)})
    (println "fail!")))
  ([source username]
   {:source source
    :username (encrypt username)
    :password (encrypt generate-password)}))

(defn read-entry []
  (* 1 1))

(defn update-entry []
  (* 1 1))

(defn delete-entry []
  (* 1 1))

;; FILESYSTEM

;; //TODO: Define behavior/wording for filename === username

(defn getpath
  "path to the userdata folder on the filesystem, configuration value"
  [user]
  (str "./data/" user ".txt"))

(defn exists?
  "Checks if a file exists, see above //TODO"
  [user]
  (true?
   (.exists (io/file (getpath user)))))

(defn read-file
  "Read data from a file and parse it"
  [user]
  (if
    (exists? user)
    (println (slurp (getpath user)))
    (println "File does not exist!")))

(defn write-to-file
  "Write a given input to a file, checks if file exists"
  [user, input]
  (if
    (exists? user)
    (spit (getpath user) input :append true)
    (println "File does not exist!")))

;(write-to-file "test" (create-user "alex" "PW123#123#123#123d"))
;(read-file "test")


;; MANAGE ENTRIES

;; //TODO: Must call read-file to read from "database"

(defn get-entry [user source-name]
  (let [entries (user :entries)]
    (filter
     #(re-matches (re-pattern source-name) (:source %))
     entries)))


;; //TODO: Must call write-to-file to persist changes

(defn set-entry [user entry]
  (let [new-user
        {:name (:name user)
         :mpw (:mpw user)
         :entries (conj (:entries user) entry)}]
    (println "User before: \n" user)
    (conj (:entries user) entry)
    (println "User after: \n" new-user)
    new-user))

;; VALIDATION

(defn valid? [pw]
  "Checks whether a password is sufficiently secure. The password must include:
   - minimum length of 10 characters
   - at least one lower case letter
   - at least one upper case letter
   - at least one digit
   - at least one special character: - @ # $ % ^ & + ="
  (let [pattern
        #"^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[-@#$%^&+=_])(?=\S+$).{10,30}$"]
    (and
     (string? pw)
     (re-matches pattern pw)
     true))) ; and returns the last value it evaluates if all are true

;;;; QUESTION: is it better to do (and bool bool true) or (boolean (and bool bool))

;; ENCRYPTION

(defn encrypt [pw]
  pw)

(defn decrypt [pw]
  pw)

;; GENERATE PASSWORD

(defn generate-password
  ([] (generate-password 10))
  ([length]
   ; Note: the range refers to the numbers assigned to chars in the ASCII charset
   ; for reference: http://www.asciitable.com
   (let [valid-chars (map char (range 33 127))] ; TODO: select proper range
     (apply str (take length (repeatedly #(rand-nth valid-chars)))))))
