(ns cljs-cursor.core-test
  (:require-macros [cljs.test :refer [deftest testing is]])
  (:require [cljs.test]
            [cljs-cursor.core :as cursor]))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 1))))

(deftest test-arithmetic []
  (is (= (+ 0.1 0.2) 0.3) "Something foul is a float."))
