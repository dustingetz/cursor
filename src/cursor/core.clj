(ns cursor.core
  (:require [cursor.root-at :refer [root-at]]))


(deftype Cursor [value, swap-fn!]
  clojure.lang.IDeref
  (deref [_] value)

  clojure.lang.IFn
  (invoke [this segments] (this segments nil))
  (invoke [this segments not-found] (this segments not-found (constantly true)))
  (invoke [this segments not-found valid?]
    (new Cursor
         (let [v (get-in value segments not-found)]
           ;; we passed the contains check e.g. (contains? {:a nil} :a) => true,
           ;; but maybe we want a nil value to count as not-found.
           (if (valid? v) v not-found))
         (fn [f]
           (swap-fn! (root-at segments (fn [v] (f (if (valid? v) v not-found))))))))

  clojure.lang.IAtom
  (swap [_ f] (swap-fn! f))
  (swap [_ f x] (swap-fn! #(f % x)))
  (swap [_ f x y] (swap-fn! #(f % x y)))
  (swap [_ f x y args] (swap-fn! #(apply f % x y args)))
  (reset [o v] (swap! o (constantly v))))


(defn cursor [store] (new Cursor @store #(swap! store %)))
