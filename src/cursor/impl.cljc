(ns cursor.impl
  (:require [cursor.root-at :refer [root-at get-in']]
            [clojure.string :as string]))


(defn invoke* [ctor deref swap-fn! segments all-segments backing-store-hash value-cursor? & {:keys [not-found invalid?]
                                                                                             :or {not-found nil
                                                                                                  invalid? (constantly false)}}]
  (let [leaf-val #(get-in' (deref) segments invalid? not-found)]
    (ctor
      leaf-val
      (fn [f]
        (swap-fn! (root-at segments f invalid? not-found)))
      (concat all-segments segments)
      backing-store-hash
      value-cursor?)))


(defn swap!* [swap-fn! f & args]
  (swap-fn! #(apply f % args)))


(defn reset!* [o v]
  (swap! o (constantly v)))


(defn hash* [deref all-segments backing-store-hash value-cursor?]
  (let [deref-hash (when value-cursor? (hash (deref)))]
    (->> [deref-hash (hash all-segments) backing-store-hash]
         (string/join "-")
         hash)))
