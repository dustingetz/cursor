(ns cursor.impl
  (:require [cursor.root-at :refer [root-at]]))


(defn deref* [ctor value swap-fn!] value)


(defn invoke* [ctor value swap-fn! segments & {:keys [not-found invalid?]
                                               :or {not-found nil
                                                    invalid? (constantly false)}}]
  (let [leaf-val (let [v (get-in value segments not-found)]
                   ;; we passed the contains check e.g. (contains? {:a nil} :a) => true,
                   ;; but maybe we want a nil value to count as not-found.
                   (if (invalid? v) not-found v))]
    (ctor
      leaf-val
      (fn [f]
        (swap-fn! (root-at segments (fn [store-leaf-val]
                                      ;; Always apply f with value the user sees, never care about what's in the
                                      ;; store, only care about the val that accounts for not-found resolution
                                      ;; (You don't want to inc the nil, you want to inc the not-found)
                                      (f leaf-val))))))))


(defn swap!* [ctor value swap-fn! f & args]
  (swap-fn! #(apply f % args)))


(defn reset!* [ctor value swap-fn! o v]
  (swap! o (constantly v)))


(defn cursor* [ctor store]
  (ctor @store #(swap! store %)))