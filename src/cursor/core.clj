(ns cursor.core
  (:require [cursor.impl :refer [deref* invoke* swap!* reset!* cursor*]]))


(deftype Cursor [value swap-fn!]
  clojure.lang.IDeref
  (deref [_]
    (deref* #(Cursor. %1 %2) value swap-fn!))

  clojure.lang.IFn
  (invoke [_ segments]
    (invoke* #(Cursor. %1 %2) value swap-fn! segments))
  (invoke [_ segments not-found]
    (invoke* #(Cursor. %1 %2) value swap-fn! segments :not-found not-found))
  (invoke [_ segments not-found invalid?]
    (invoke* #(Cursor. %1 %2) value swap-fn! segments :not-found not-found :invalid? invalid?))

  clojure.lang.IAtom
  (swap [_ f]
    (swap!* #(Cursor. %1 %2) value swap-fn! f))
  (swap [_ f x]
    (swap!* #(Cursor. %1 %2) value swap-fn! f x))
  (swap [_ f x y]
    (swap!* #(Cursor. %1 %2) value swap-fn! f x y))
  (swap [_ f x y args]
    (apply swap!* #(Cursor. %1 %2) value swap-fn! f x y args))
  (reset [o v]
    (reset!* #(Cursor. %1 %2) value swap-fn! o v)))


(defn cursor [store] (cursor* #(Cursor. %1 %2) store))
