#?(:clj  (ns cljs-cursor.core-test
             (:require [clojure.test :refer [deftest testing is]]
                       [cljs-cursor.core :as cursor]))
   :cljs (ns cljs-cursor.core-test
           (:require-macros [cljs.test :refer [deftest testing is]])
           (:require [cljs.test]
                     [cljs-cursor.core :as cursor])))


(deftest test-root-at []
  (is (= (let [f (cursor/root-at [:a :b] inc)]
           (f {:a {:b 0}}))
         {:a {:b 1}})))


(deftest test-value []
  (let [store (atom {:a 0})
        cur (cursor/buildCursor store)]
    (is (= (cursor/value cur) {:a 0}))))


(deftest test-swap []
  (let [store (atom 0)
        cur (cursor/buildCursor store)]
    (cursor/swap cur inc)
    (is (= @store 1))))


(deftest test-refine []
  (let [store (atom {:a 0})
        cur (cursor/buildCursor store)]
  (is (= (-> cur (cursor/refine [:a]) cursor/value) 0))))

(deftest refine-not-found []
  (let [store (atom {:a 0})
        cur (cursor/buildCursor store)]
    (is (= (-> cur (cursor/refine [:b] 42) cursor/value) 42))))


(def initialMap {:a {:b 1}, :xs [1 2 3]})
(def store (atom initialMap))
(def cur (cursor/buildCursor store))


(deftest identity-swap1 []
  (-> cur (cursor/swap identity))
  (is (= @store initialMap)))


(deftest test-default-refines []
  (is (= (-> cur (cursor/refine [:c] {:d 10}) (cursor/refine [:d]) cursor/value) 10))
  (let [dcur (-> cur (cursor/refine [:c] {:d 10}) (cursor/refine [:d]))]
    (cursor/swap dcur identity)
    (is (= (cursor/value dcur) 10))
    (cursor/swap dcur (constantly 11))
    (is (= (cursor/value dcur) 10))
    (is (= (-> (cursor/buildCursor store) (cursor/refine [:c :d]) cursor/value) 11))))

(deftest swap-merge []
         (-> cur (cursor/refine [:a]) (cursor/swap #(merge % {:z 99})))
         (is (= (get-in @store [:a :z]) 99)))
