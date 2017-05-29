(ns cursor.ref-cursor
  (:require [cursor.impl :refer [deref* invoke* swap!* reset!* hash*]]))


(declare ->RefCursor)

(deftype RefCursor [deref! swap-fn! all-segments backing-store-hash]
  IDeref
  (-deref [_] (deref!))

  IFn
  (-invoke [_ segments]
    (invoke* ->RefCursor deref! swap-fn! segments all-segments backing-store-hash))
  (-invoke [_ segments not-found]
    (invoke* ->RefCursor deref! swap-fn! segments all-segments backing-store-hash :not-found not-found))
  (-invoke [_ segments not-found invalid?]
    (invoke* ->RefCursor deref! swap-fn! segments all-segments backing-store-hash :not-found not-found :invalid? invalid?))

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
    (hash* (deref!) all-segments backing-store-hash))

  IEquiv
  (-equiv [o o']
    (and (instance? RefCursor o') (= (hash o) (hash o')))))
