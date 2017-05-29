(ns cursor.ref-cursor
  (:require [cursor.impl :refer [deref* invoke* swap!* reset!* hash*]]))


(declare ->RefCursor)

(deftype RefCursor [deref! swap-fn! all-segments backing-store-hash]
  clojure.lang.IDeref
  (deref [_] (deref!))

  clojure.lang.IFn
  (invoke [_ segments]
    (invoke* ->RefCursor deref! swap-fn! segments all-segments backing-store-hash))
  (invoke [_ segments not-found]
    (invoke* ->RefCursor deref! swap-fn! segments all-segments backing-store-hash :not-found not-found))
  (invoke [_ segments not-found invalid?]
    (invoke* ->RefCursor deref! swap-fn! segments all-segments backing-store-hash :not-found not-found :invalid? invalid?))

  clojure.lang.IAtom
  (swap [_ f]
    (swap!* swap-fn! f))
  (swap [_ f x]
    (swap!* swap-fn! f x))
  (swap [_ f x y]
    (swap!* swap-fn! f x y))
  (swap [_ f x y args]
    (apply swap!* swap-fn! f x y args))

  ;; reset is part of IAtom in jvm, but split into IReset in cljs
  (reset [o v]
    (reset!* o v))

  clojure.lang.IHashEq
  (hasheq [_]
    (hash* (deref!) all-segments backing-store-hash))

  Object
  (equals [o o']
    (and (instance? RefCursor o') (= (hash o) (hash o')))))
