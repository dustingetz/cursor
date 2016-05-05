(ns cursor.impl
  (:require [cursor.root-at :refer [root-at]]))


(defn deref* [ctor value swap-fn!] value)


(defn invoke* [ctor value swap-fn! segments not-found valid?]
  (ctor
    (let [v (get-in value segments not-found)]
      ;; we passed the contains check e.g. (contains? {:a nil} :a) => true,
      ;; but maybe we want a nil value to count as not-found.
      (if (valid? v) v not-found))
    (fn [f]
      (swap-fn! (root-at segments (fn [v] (f (if (valid? v) v not-found))))))))


(defn swap!* [ctor value swap-fn! f & args]
  (swap-fn! #(apply f % args)))


(defn reset!* [ctor value swap-fn! o v]
  (swap! o (constantly v)))


(defn cursor* [ctor store]
  (ctor @store #(swap! store %)))