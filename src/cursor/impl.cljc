(ns cursor.impl
  (:require [cursor.root-at :refer [root-at]]
            [clojure.string :as string]))


(defn deref* [value] value)


(defn invoke* [ctor value swap-fn! segments all-segments store & {:keys [not-found invalid?]
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
                                      (f leaf-val)))))
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
