(ns cursor.core
  (:require [cursor.impl :refer [deref* invoke* swap!* reset!* cursor* hash*]]))


(declare ctor)


(deftype Cursor [value swap-fn! all-segments store]
  clojure.lang.IDeref
  (deref [_]
    (deref* value))

  clojure.lang.IFn
  (invoke [_ segments]
    (invoke* ctor value swap-fn! segments all-segments store))
  (invoke [_ segments not-found]
    (invoke* ctor value swap-fn! segments all-segments store :not-found not-found))
  (invoke [_ segments not-found invalid?]
    (invoke* ctor value swap-fn! segments all-segments store :not-found not-found :invalid? invalid?))

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
    (hash* value all-segments store))

  Object
  (equals [o other]
    (and (instance? Cursor other) (= (hash o) (hash other)))))


(defn- ctor [value swap-fn! all-segments store]
  (Cursor. value swap-fn! all-segments store))


(defn cursor [store] (cursor* ctor store))
