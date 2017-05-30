(ns cursor.cursor
  (:require [cursor.impl :refer [invoke* swap!* reset!* hash*]]))


(declare ->Cursor)

(deftype Cursor [deref swap-fn! all-segments backing-store-hash value-cursor?]
  IDeref
  (-deref [_] (deref))

  IFn
  (-invoke [_ segments]
    (invoke* ->Cursor deref swap-fn! segments all-segments backing-store-hash value-cursor?))
  (-invoke [_ segments not-found]
    (invoke* ->Cursor deref swap-fn! segments all-segments backing-store-hash value-cursor? :not-found not-found))
  (-invoke [_ segments not-found invalid?]
    (invoke* ->Cursor deref swap-fn! segments all-segments backing-store-hash value-cursor? :not-found not-found :invalid? invalid?))

  ISwap
  (-swap! [_ f]
    (swap!* swap-fn! f))
  (-swap! [_ f x]
    (swap!* swap-fn! f x))
  (-swap! [_ f x y]
    (swap!* swap-fn! f x y))
  (-swap! [_ f x y args]
    (apply swap!* swap-fn! f x y args))

  IReset
  (-reset! [o v]
    (reset!* o v))

  IHash
  (-hash [_]
    (hash* deref all-segments backing-store-hash value-cursor?))

  IEquiv
  (-equiv [o o']
    (and (instance? Cursor o') (= (hash o) (hash o')))))
