(ns cljs-cursor.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [cljs-cursor.core :as cursor]))


(deftest test-root-at []
  (is (= (let [f (cursor/root-at [:a :b] inc)]
           (f {:a {:b 0}}))
         {:a {:b 1}})))


(deftest test-value []
  (let [store (atom {:a 0})
        cur (cursor/buildCursor store)]
    (is (= @store {:a 0}))))


(deftest test-swap []
  (let [store (atom 0)
        cur (cursor/buildCursor store)]
    (.swap cur inc)
    (is (= @store 1))))


(deftest test-refine []
  (let [store (atom {:a 0})
        cur (cursor/buildCursor store)]
  (is (= (-> cur (.refine [:a]) .value) 0))))

(deftest refine-not-found []
  (let [store (atom {:a 0})
        cur (cursor/buildCursor store)]
    (is (= (-> cur (.refine [:b] 42) .value) 42))))


(def initialMap {:a {:b 1}, :xs [1 2 3]})
(def store (atom initialMap))
(def cur (cursor/buildCursor store))


(deftest identity-swap1 []
  (-> cur (.swap identity))
  (is (= @store initialMap)))


(deftest identity-swap2 []
  (is (= (-> cur (.refine [:c] {:d 10}) (.refine [:d]) .value) 10))
  (let [dcur (-> cur (.refine [:c] {:d 10}) (.refine [:d]))]
    (.swap dcur identity)
    (is (= (.value dcur) 10)))
)