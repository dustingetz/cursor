(ns cursor.impl
  (:require [cursor.root-at :refer [root-at get-in']]
            [clojure.string :as string]))


(defn deref* [value] value)


(defn invoke* [ctor value swap-fn! segments all-segments store & {:keys [not-found invalid?]
                                                                     :or {not-found nil
                                                                          invalid? (constantly false)}}]
  (let [leaf-val (get-in' value segments invalid? not-found)]
    (ctor
      leaf-val
      (fn [f]
        (swap-fn! (root-at segments f invalid? not-found)))
      (concat all-segments segments)
      store)))


(defn swap!* [swap-fn! f & args]
  (swap-fn! #(apply f % args)))


(defn reset!* [o v]
  (swap! o (constantly v)))


(defn hash* [& args]
  (->> (map hash args)
       (string/join "-")
       hash))


(defn cursor* [ctor store]
  ;; Because of platform differences, this needs to be abstracted into a
  ;; pure clojure function, per our understanding.
  (ctor @store #(swap! store %) [] store))
