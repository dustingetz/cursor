(ns cursor.core
  (:require [cursor.impl :refer [deref* invoke* swap!* reset!* cursor* hash*]]))


(declare ->Cursor)


(deftype Cursor [value swap-fn! all-segments store]
  clojure.lang.IDeref
  (deref [_]
    (deref* value))

  clojure.lang.IFn
  (invoke [_ segments]
    (invoke* ->Cursor value swap-fn! segments all-segments store))
  (invoke [_ segments not-found]
    (invoke* ->Cursor value swap-fn! segments all-segments store :not-found not-found))
  (invoke [_ segments not-found invalid?]
    (invoke* ->Cursor value swap-fn! segments all-segments store :not-found not-found :invalid? invalid?))

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
  (equals [o o']
    (and (instance? Cursor o') (= (hash o) (hash o')))))


(defn cursor [store] (cursor* ->Cursor store))


(deftype WriteOnlyCursor [cur]
  clojure.lang.IFn
  (invoke [_ segments]
    (WriteOnlyCursor. (cur segments)))
  (invoke [_ segments not-found]
    (WriteOnlyCursor. (cur segments not-found)))
  (invoke [_ segments not-found invalid?]
    (WriteOnlyCursor. (cur segments not-found invalid?)))

  clojure.lang.IAtom
  (swap [_ f]
    (swap! cur f))
  (swap [_ f x]
    (swap! cur f x))
  (swap [_ f x y]
    (swap! cur f x y))
  (swap [_ f x y args]
    (apply swap! cur f x y args))

  ;; reset is part of IAtom in jvm, but split into IReset in cljs
  (reset [_ v]
    (reset! cur v))

  clojure.lang.IHashEq
  (hasheq [_]
    (hash* (.all-segments cur) (.store cur)))

  Object
  (equals [o o']
    (and (instance? WriteOnlyCursor o') (= (hash o) (hash o')))))
