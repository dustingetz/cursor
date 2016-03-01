#?(:clj  (ns cljs-cursor.core-test
             (:require [clojure.test :refer [deftest testing is]]
                       [cljs-cursor.cursor :as cursor]
                       [cljs-cursor.root-at :refer [root-at]]))
   :cljs (ns cljs-cursor.core-test
           (:require-macros [cljs.test :refer [deftest testing is]])
           (:require [cljs.test]
                     [cljs-cursor.cursor :as cursor]
                     [cljs-cursor.root-at :refer [root-at]])))


(deftest test-root-at []
  (is (= (let [f (root-at [:a :b] inc)]
           (f {:a {:b 0}}))
         {:a {:b 1}})))


(deftest test-value []
  (let [store (atom {:a 0})
        cur (cursor/buildCursor store)]
    (is (= @cur {:a 0}))))


(deftest test-swap []
  (let [store (atom 0)
        cur (cursor/buildCursor store)]
    (swap! cur inc)
    (is (= @store 1))))


(deftest test-refine []
  (let [store (atom {:a 0})
        cur (cursor/buildCursor store)]
  (is (=  @(cur [:a]) 0))))

(deftest refine-not-found []
  (let [store (atom {:a 0})
        cur (cursor/buildCursor store)]
    (is (= @(cur [:b] 42) 42))))


(def initialMap {:a {:b 1}, :xs [1 2 3]})

(deftest identity-swap1 []
         (let [store (atom initialMap)
               cur (cursor/buildCursor store)]

              (-> cur (swap! identity))
              (is (= @store initialMap))))


(deftest test-default-refines []
         (let [store (atom initialMap)
               cur (cursor/buildCursor store)]
              (is (= @((cur [:c] {:d 10}) [:d]) 10))
              (let [dcur ((cur [:c] {:d 10}) [:d])]
                   (swap! dcur identity)
                   (is (= @dcur 10))
                   (swap! dcur (constantly 11))
                   (is (= @dcur 10))
                   (is (= @((cursor/buildCursor store) [:c :d]) 11)))))

(deftest swap-merge []
         (let [store (atom initialMap)
               cur (cursor/buildCursor store)]
              (-> (cur [:a]) (swap! merge {:z 99}))
              (is (= (get-in @store [:a :z]) 99))))


(deftest reset []
         (let [store (atom initialMap)
               cur (cursor/buildCursor store)]
              (is (= @store @cur))
              (swap! (cur [:a]) (constantly 42))
              (is (not= @store @cur))
              (is (= (get-in @store [:a]) 42))
              (reset! (cur [:a]) 43)
              (is (= (get-in @store [:a]) 43)))
         )