module CheckersLachlanMoore where

    import GameLogic
    import Lens.Micro.Platform
    import Data.List
    import Data.Ord
    
    type Moves = GameState -> [Move]
    
    type Red_ai = GameState -> Move
    
    type Black_ai = GameState -> Move
    
    --Lachlan Moore, 30030228
    --CPSC 449, Fall 2019
    --More info in readme
    
    --Apply move
    apply_move :: ApplyMove
    apply_move mv g 
            | member mv (move g) = apply_move' mv g
            | length(move g) <= 0 && (_status g) == Red = g {_status = GameOver
                                                         , _message = "Game over! Black wins!"}
            | length(move g) <= 0 && (_status g) == Black = g {_status = GameOver
                                                         , _message = "Game over! Red wins!"}
            | otherwise = g {_message = "Not a legal move, try again!"}
    --Helper for the apply_move
    apply_move' :: ApplyMove
    apply_move' mv g
            | member start (_redKings g) = g {_redKings = replace start end (_redKings g)
                                            , _blackPieces = jumpedP mv (_blackPieces g) 
                                            , _blackKings = jumpedP mv (_blackKings g)
                                            , _status = changePlayer (_status g)
                                            , _message = ""}
            | member start (_redPieces g) 
                   && promoteToKing g end = g {_redPieces = remove start (_redPieces g)
                                            , _redKings = end:(_redKings g)
                                            , _blackPieces = jumpedP mv (_blackPieces g)
                                            , _blackKings = jumpedP mv (_blackKings g)
                                            , _status = changePlayer (_status g)
                                            , _message = ""}
            | member start (_redPieces g) = g {_redPieces = replace start end (_redPieces g)
                                            , _redKings = (_redKings g)
                                            , _blackPieces = jumpedP mv (_blackPieces g)
                                            , _blackKings = jumpedP mv (_blackKings g)
                                            , _status = changePlayer (_status g)
                                            , _message = ""}
            | member start (_blackKings g) = g {_blackKings = replace start end (_blackKings g)
                                            , _redPieces = jumpedP mv (_redPieces g)
                                            , _redKings = jumpedP mv (_redKings g)
                                            , _status = changePlayer (_status g)
                                            , _message = ""}
            | member start (_blackPieces g)
                     && promoteToKing g end = g {_blackPieces = remove start (_blackPieces g)
                                            , _blackKings = end:(_blackKings g)
                                            , _redPieces = jumpedP mv (_redPieces g)
                                            , _redKings = jumpedP mv (_redKings g)
                                            , _status = changePlayer (_status g)
                                            , _message = ""}
            | member start (_blackPieces g) = g {_blackPieces = replace start end (_blackPieces g)
                                            , _blackKings = (_blackKings g)
                                            , _redPieces = jumpedP mv (_redPieces g)
                                            , _redKings = jumpedP mv (_redKings g)
                                            , _status = changePlayer (_status g)
                                            , _message = ""}
            | otherwise = g {_message = "Can't handle this yet"}
      where
            start = head(mv)
            end = last(mv)

    --Checkers and other usefull functions--
    --Checks to see if a piece at a given coord is able to be promoted to a King
    promoteToKing :: GameState -> Coord -> Bool
    promoteToKing g (x, y) | (_status g) == Red && (y == 0) = True
                           | (_status g) == Black && (y == 7) = True
                           | otherwise = False
    --Function that returns a list of pieces with the removed jumped pieces
    jumpedP :: Move -> [Coord] -> [Coord]
    jumpedP [] pieces = pieces
    jumpedP (x:xs) pieces | length(xs) < 1 = pieces
                          | isJump x (head(xs)) = jumpedP xs (remove(pieceToRemove x (head(xs))) pieces)
                          | otherwise = jumpedP xs pieces
    --Finds a jumped piece to be removed
    pieceToRemove :: Coord -> Coord -> Coord
    pieceToRemove (x, y) (a, b) = ((quot (a+x) 2), (quot (b+y) 2))
    --Checks to see if a given move is a jump
    isJump :: Coord -> Coord -> Bool
    isJump (x, y) (a, b) | ((x - a), (y - b)) == (2, 2) || ((x - a), (y - b)) == (-2, -2) || ((x - a), (y - b)) == (2, -2) || ((x - a), (y - b)) == (-2, 2) = True
                         | otherwise = False
    --Removes a given Coord from a list of Coords
    remove :: Coord -> [Coord] -> [Coord]
    remove element list = filter (\e -> e/= element) list

    --Checks to see if the given thing is a member of a list of things
    member :: (Eq a) => a -> [a] -> Bool
    member x [] = False
    member x (a:as) | x == a = True
                    | otherwise = member x as
    --Used to actually move a piece in apply_move  
    replace :: Coord -> Coord -> [Coord] -> [Coord]
    replace start end [] = []
    replace start end (a:as) | a == start = end:as
                             | otherwise = a:(replace start end as)
    --Turn switcher
    changePlayer :: Status -> Status
    changePlayer Red = Black
    changePlayer Black = Red

    --Checks to see if the given Coord holds a black piece or king
    isBlack :: GameState -> Coord -> Bool
    isBlack g x
          | x `elem` (_blackPieces g) ++ (_blackKings g) = True
          | otherwise = False
    
    --Checks to see if the given Coord holds a red piece or king
    isRed :: GameState -> Coord -> Bool
    isRed g x
          | x `elem` (_redPieces g) ++ (_redKings g) = True
          | otherwise = False

    --Checks to see if the given Coord is empty
    isEmpty :: GameState -> Coord -> Bool
    isEmpty g x 
          | x `elem` (_redPieces g) = False
          | x `elem` (_blackPieces g) = False
          | x `elem` (_redKings g) = False
          | x `elem` (_blackKings g) = False
          | otherwise = True

    --All moves--
    
    move :: GameState -> [Move]
    move g
            | length (jump_moves g) <= 0 = simple_moves g
            | otherwise = jump_moves g 

    --Finds all simple moves
    simple_moves :: GameState -> [Move]
    simple_moves g
            | g^.status == Red = simple_Redmoves g ++ simple_RedKingmoves g
            | otherwise = simple_Blackmoves g ++ simple_BlackKingmoves g
    
    --Finds all simple redmoves
    simple_Redmoves :: GameState -> [Move]
    simple_Redmoves g = [[(x, y),(a, b)] | (x, y) <- (_redPieces g) 
                                         , (a, b) <- [(x-1, y-1), (x+1, y-1)]
                                         , a >= 0 && a <= 7 && b >= 0 && b <= 7
                                         , isEmpty g (a,b)]
    --Finds all simple blackmoves
    simple_Blackmoves :: GameState -> [Move]
    simple_Blackmoves g = [[(x, y),(a, b)] | (x, y) <- (_blackPieces g) 
                                         , (a, b) <- [(x-1, y+1), (x+1, y+1)]
                                         , a >= 0 && a <= 7 && b >= 0 && b <= 7
                                         , isEmpty g (a,b)]
    --Finds all simple kingmoves
    simple_BlackKingmoves :: GameState -> [Move]
    simple_BlackKingmoves g = [[(x, y),(a, b)] | (x, y) <- (_blackKings g)
                                         , a <- [x+1, x-1]
                                         , b <- [y+1, y-1]
                                         , a >= 0 && a <= 7 && b >= 0 && b <= 7
                                         , isEmpty g (a,b)]
    --Finds all simple kingmoves
    simple_RedKingmoves :: GameState -> [Move]
    simple_RedKingmoves g = [[(x, y),(a, b)] | (x, y) <- (_redKings g)
                                         , a <- [x+1, x-1]
                                         , b <- [y+1, y-1]
                                         , a >= 0 && a <= 7 && b >= 0 && b <= 7
                                         , isEmpty g (a,b)]

    --Finds all jump moves
    jump_moves :: GameState -> [Move]
    jump_moves g
            | g^.status == Red = jump_Red g ++ jump_King g (_redKings g)
            | otherwise = jump_Black g ++ jump_King g (_blackKings g)

    --Finds all Black jump moves
    jump_Black :: GameState -> [Move]
    jump_Black g = [(x, y):ys | (x, y) <- (_blackPieces g), ys <- jump_Black' g (x, y) [] (x, y)]
    --Helper for above function                            
    jump_Black' :: GameState -> Coord -> [Coord] -> Coord -> [Move]
    jump_Black' g start rem (x, y) = [(x'', y''):ys | ((x', y'),(x'', y'')) <- [((x+1,y+1),(x+2,y+2)),((x-1,y+1),(x-2,y+2))]
                                    , not (member (x', y') rem) && opponentOccupied g (x', y') && (start == (x'', y'') || notOccupied g (x'', y'')) && inBounds (x'', y'')
                                    , ys <- jump_over (jump_Black' g start ((x',y'):rem) (x'',y''))]
    --Finds all Red jump moves
    jump_Red :: GameState -> [Move]
    jump_Red g = [(x, y):ys | (x, y) <- (_redPieces g), ys <- jump_Red' g (x, y) [] (x, y)]
                          
    jump_Red' :: GameState -> Coord -> [Coord] -> Coord -> [Move]
    jump_Red' g start rem (x, y) = [(x'', y''):ys | ((x', y'),(x'', y'')) <- [((x+1,y-1),(x+2,y-2)),((x-1,y-1),(x-2,y-2))]
                                    , not (member (x', y') rem) && opponentOccupied g (x', y') && (start == (x'', y'') || notOccupied g (x'', y'')) && inBounds (x'', y'')
                                    , ys <- jump_over (jump_Red' g start ((x',y'):rem) (x'',y''))]
    --Finds all King jump moves, takes in the type of Kings to use
    jump_King :: GameState -> [Coord] -> [Move]
    jump_King g xs = [(x, y):ys | (x, y) <- xs, ys <- jump_King' g (x, y) [] (x, y)]

    jump_King' :: GameState -> Coord -> [Coord] -> Coord -> [Move]
    jump_King' g start rem (x, y) = [(x'', y''):ys | ((x', y'),(x'', y'')) <- [((x+1,y+1),(x+2,y+2)),((x-1,y+1),(x-2,y+2)),((x+1,y-1),(x+2,y-2)),((x-1,y-1),(x-2,y-2))]
                                    , not (member (x', y') rem) && opponentOccupied g (x', y') && (start == (x'', y'') || notOccupied g (x'', y'')) && inBounds (x'', y'')
                                    , ys <- jump_over (jump_King' g start ((x',y'):rem) (x'',y''))]
    --Helper function for jump_King'           
    jump_over :: [Move] -> [Move]
    jump_over [] = [[]]
    jump_over z = z
    
    --Checker to see if the given spot is occupied by the opponents piece
    opponentOccupied :: GameState -> Coord -> Bool
    opponentOccupied g (x, y) | (_status g) == Red && isBlack g (x, y) = True
                              | (_status g) == Black && isRed g (x, y) = True
                              | otherwise = False
    --Checker to see if the given spot is not occupied by any pieces
    notOccupied :: GameState -> Coord -> Bool
    notOccupied g (x, y) | member (x, y) ((_redPieces g) ++ (_redKings g) ++ (_blackPieces g) ++ (_blackKings g)) = False
                         | otherwise = True

    --Checker to see if the give move location is still in the boundary of the board
    inBounds :: Coord -> Bool
    inBounds (x, y) | x >= 0 && x <= 7 && y >= 0 && y <= 7 = True
                    | otherwise = False


    --AI SECTION BELOW--
    
    --Pratice/Back-up if I can't understand how to code a minmax tree in haskell.
    --Ai that looks for the best move out of the list of moves given and uses that
    --move. Ie. Simple AI

    --Backup (not very smart AI)
    red_ai :: Red_ai
    red_ai g = red_ai' g (move g)

    red_ai' :: GameState -> [Move] -> Move
    red_ai' g ms = ms!!getSecond(maxLoc(maxi g ms))

    black_ai :: Black_ai
    black_ai g = black_ai' g (move g)

    black_ai' :: GameState -> [Move] -> Move
    black_ai' g ms = ms!!getSecond(maxLoc(mini g ms))

    maxi :: GameState -> [Move] -> [Int]
    maxi g ms = [red_heuristic (apply_move m g) | m <- ms]

    mini :: GameState -> [Move] -> [Int]
    mini g ms = [black_heuristic (apply_move m g) | m <- ms]

    maxLoc :: (Ord a) => [a] -> (a, Int)
    maxLoc xs = maximumBy (comparing fst) (zip xs [0..])

    getSecond :: (a, Int) -> Int
    getSecond (x, y) = y

    red_heuristic :: GameState -> Int
    red_heuristic g = (length(_redPieces g)) - (length(_blackPieces g)) + 2*((length(_redKings g)) - (length(_blackKings g)))

    black_heuristic :: GameState -> Int
    black_heuristic g = (length(_blackPieces g)) - (length(_redPieces g)) + 2*((length(_blackKings g)) - (length(_redKings g)))

    --Step one, Take the list of all possible moves from the original GameState
    --Create a rose tree using those moves as the branches to new possible GameStates
    --as those respected GameStates as there new nodes. *Note want to look 3-4 moves
    --ahead to start.

    data Rose a = Node a [Rose a]
        deriving (Eq, Show)
    --Not sure how to build a rose tree given a list of moves
    --Should it hold values of int? If so how do I get the best
    --move back from that? Should it hold GameStates and then
    --find the move from that? Seems ineffiecent...

    --pseudocode from wiki
    {-
    function alphabeta(node, depth, α, β, maximizingPlayer) is
    if depth = 0 or node is a terminal node then
        return the heuristic value of node
    if maximizingPlayer then
        value := −∞
        for each child of node do
            value := max(value, alphabeta(child, depth − 1, α, β, FALSE))
            α := max(α, value)
            if α ≥ β then
                break (* β cut-off *)
        return value
    else
        value := +∞
        for each child of node do
            value := min(value, alphabeta(child, depth − 1, α, β, TRUE))
            β := min(β, value)
            if α ≥ β then
                break (* α cut-off *)
        return value
        -}

    --Now still have the problem, how do I make a tree with the GameStates and moves
    --and know which move is actually the best one? I understand the algorthim...
    --Not sure how to apply it to the given code structure. I hope that me writing
    --this out still gets some marks...
    
    --basic minmax algorthim
    minimax :: (Num a, Ord a) => Rose a -> a
    minimax (Node n []) = n
    minimax (Node n ts) = - minimum (map minimax ts)

    --minmax with alpha beta prunning
    abminimax :: (Num a, Ord a) => a -> a -> Rose a -> a
    abminimax a b (Node n []) = a `max` n `min` b
    abminimax a b (Node n ts) = helper a ts
         where
                helper a [] = a
                helper a (t:ts) | a' >= b = a'
                             | otherwise = helper a' ts
                             where a' = -(abminimax (-b) (-a) t)
        
    --Helper functions with trees

    --Finds all paths of a tree, used to find the path the Ai wants to take
    paths :: Rose a -> [[a]]
    paths (Node n []) = [[n]]
    paths (Node n ts) = map ((:) n . concat . paths) ts

    --Prune a tree to a fixed depth using arg Int
    prune :: Int -> Rose a -> Rose a
    prune x (Node n ts) | x > 0 = Node n (map (prune (x-1)) ts)
                        | otherwise = Node n []
    
    --Find the depth of a given tree
    depth :: Rose a -> Int
    depth (Node n []) = 1
    depth (Node n ts) = 1 + maximum (map depth ts)

    --Find the width of a given tree
    width :: Rose a -> Int
    width (Node n ts) = length ts
