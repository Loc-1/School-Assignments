--Assignment 1, CPSC 449 Fall 2019
--Lachlan Moore, 30030228

import Data.Function
import Data.List
import Data.Ord

--1. Take average of 3 integers and return it as a float
avgThree :: Int -> Int -> Int -> Float
avgThree a b c = (fromIntegral a + fromIntegral b + fromIntegral c)/3.0

--2. Take the maximum of 3 integers and also return how many times it occurs
maxThree :: Int -> Int -> Int -> (Int, Int)
maxThree a b c
   | a == b && b == c = (a, 3)
   | a == max a (max b c) && b == max a (max b c) && c /= max a (max b c) = (a, 2)
   | a == max a (max b c) && c == max a (max b c) && b /= max a (max b c) = (a, 2)
   | b == max a (max b c) && c == max a (max b c) && a /= max a (max b c) = (b, 2)
   | otherwise = (max a (max b c), 1)

--3. Returns the largest number whose factorial is no greater than the given number
--Commented out for now dont know why it doesnt work
--invFac :: Integer -> Integer
--invFac n = helperF n n

--helperF :: Int -> Int -> Int
--helperF 0 x = 1
--helperF n x 
--   | ((fac n) <= x) = n
--   | otherwise = helperF(n - 1) x

--fac :: Int -> Int
--fac 0 = 1
--fac n = n * fac(n-1)

--4. Take two ints and return the greatest common divisor using Euclid's Algorithm
-- Naming this gcd' idk how to overwrite prelude functions
gcd' :: Integer -> Integer -> Integer
gcd' x 0 = abs x
gcd' x y = gcd b (mod a b)
   where a = abs x
         b = abs y

--5. Find binomial coefficient
--Formual found at https://rosettacode.org/wiki/Evaluate_binomial_coefficients#Haskell
binom :: Integer -> Integer -> Integer
binom n k = product [k+1..n] `div` product [1..n-k]

--6. Grows a strings length
grow :: String -> String
grow a = undefined

--7. Tests wether the list is strictly increasing
instrictorder :: [Integer] -> Bool
instrictorder [] = True
instrictorder [x] = True
instrictorder (x:(y:ys))
   | (x <= y) = instrictorder (y:ys)
   | otherwise = False

--8. Returns a list of items below a given threshold cost
cheapItems :: [(String, Integer)] -> Integer -> [String]
cheapItems = undefined

--9. Given a list of items sorts them by there cost
sortByCost :: [(String, Integer)] -> [(String, Integer)]
sortByCost x = undefined

--10. Calculates a list (ascending order) of all divisors of a positive integer
divisors :: Integer -> [Integer]
divisors x 
   | x < 1 = []
   | otherwise = filter ((== 0) . (mod x)) [1..x]
   

--11. Determines whether a given string is a substring of the other
substring :: String -> String -> Bool
substring (x:xs) [] = False
substring xs ys
   | helperS xs ys = True
   | substring xs (tail ys) = True
   | otherwise = False

helperS :: String -> String -> Bool
helperS [] ys = True
helperS (x:xs) [] = False
helperS (x:xs) (y:ys) = (x == y) && helperS xs ys

--12. Returns the list of all sublists of that list.
sublists :: [a] -> [[a]]
sublists [] = [[]]
sublists (x:xs) = subs [] (x:xs) ++ sublists xs
   where
      subs :: [a] -> [a] -> [[a]]
      subs xs [] = [xs]
      subs xs (a:as) = (xs ++ [a]) : (subs (xs ++ [a]) as)