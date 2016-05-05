(ns cursor.core
  (:require [cursor.impl :refer [cursor-deref cursor-invoke cursor-swap! cursor-reset! cursor-cursor]]))


(deftype Cursor [value swap-fn!]
  IDeref
  (-deref [_] (cursor-deref #(Cursor. %1 %2) value swap-fn! value))

  IFn
  (-invoke [_ segments] (cursor-invoke #(Cursor. %1 %2) value swap-fn! segments nil (constantly true)))
  (-invoke [_ segments not-found] (cursor-invoke #(Cursor. %1 %2) value swap-fn! segments not-found (constantly true)))
  (-invoke [_ segments not-found valid?] (cursor-invoke #(Cursor. %1 %2) value swap-fn! segments not-found valid?))

  ISwap
  (-swap! [_ f] (cursor-swap! #(Cursor. %1 %2) value swap-fn! f))
  (-swap! [_ f x] (cursor-swap! #(Cursor. %1 %2) value swap-fn! f x))
  (-swap! [_ f x y] (cursor-swap! #(Cursor. %1 %2) value swap-fn! f x y))
  (-swap! [_ f x y args] (apply cursor-swap! #(Cursor. %1 %2) value swap-fn! f x y args))

  IReset
  (-reset! [o v] (cursor-reset! #(Cursor. %1 %2) value swap-fn! o v)))


(defn cursor [store] (cursor-cursor #(Cursor. %1 %2) store))
