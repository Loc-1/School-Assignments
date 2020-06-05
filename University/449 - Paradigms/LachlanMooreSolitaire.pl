% Lachlan Moore, 30030228
% CPSC 449, Fall 2019

% Crossbow is pretty much instant
% Longbow is taking about 7 seconds
% Half dead takes about 3 seconds
% Not quite dead is taking about 70 seconds... not sure why it is taking longer then Full
% Full is taking about 7 seconds
% Also have just been running this on the SWISH website so that may be why some of these times are a little slow...

% ---- HOW TO RUN ----
% peg
% I hope that displaying the board like this is fine
% peg(crossbow) FOR CROSSBOW SOLUTION
% peg(longbow) FOR LONGBOW SOLUTION
% peg(halfdead) FOR HALF DEAD SOLUTION
% peg(notqdead) FOR NOT QUITE DEAD SOLUTION
% peg(full) FOR FULL SOLUTION
peg(crossbow) :- 
    solitare_steps([31,32,34,35,41,42,43,44,45,53], X, [3]),
    write("Start Board: (X = pegs, - = empty, 0 = goal)"), nl,
    write("- - | 0 1 2 3 4 5 6 | "), nl,
    write("0 0 - - | - 0 - | - - "), nl,
    write("1 0 - - | - - - | - - "), nl,
    write("2 0 | - - - - - - - | "), nl,
    write("3 0 | - X X - X X - | "), nl,
    write("4 0 | - X X X X X - | "), nl,
    write("5 0 - - | - X - | - - "), nl,
    write("6 0 - - | - - - | - - "), nl,
    write("Moves to solution: "), write(X).
peg(longbow) :-
    solitare_steps([20,26,30,31,33,35,36,41,43,45,52,53,54,63], X, [3]),
    write("Start Board: (X = pegs, - = empty, 0 = goal)"), nl,
    write("- - | 0 1 2 3 4 5 6 | "), nl,
    write("0 0 - - | - 0 - | - - "), nl,
    write("1 0 - - | - - - | - - "), nl,
    write("2 0 | X - - - - - X | "), nl,
    write("3 0 | X X - X - X X | "), nl,
    write("4 0 | - X - X - X - | "), nl,
    write("5 0 - - | X X X | - - "), nl,
    write("6 0 - - | - X - | - - "), nl,
    write("Moves to solution: "), write(X).
peg(halfdead) :-
    fast_steps(halfdead, [20,22,23,24,30,34,35,40,41,42,43,44,45,52,54,62,64], [], X),
    write("Start Board: (X = pegs, - = empty, 0 = goal)"), nl,
    write("- - | 0 1 2 3 4 5 6 | "), nl,
    write("0 0 - - | - - - | - - "), nl,
    write("1 0 - - | - - - | - - "), nl,
    write("2 0 | X - X X X - - | "), nl,
    write("3 0 | X - - 0 X X - | "), nl,
    write("4 0 | X X X X X X - |"), nl,
    write("5 0 - - | X - X | - - "), nl,
    write("6 0 - - | X - X | - - "), nl,
    write("Moves to solution: "), write(X).
peg(notqdead) :-
    fast_steps(notqdead, [2,3,4,12,14,20,21,22,23,24,25,26,30,32,35,36,40,41,42,43,44,45,46,52,54,62,64], [], X),
    write("Start Board: (X = pegs, - = empty, 0 = goal)"), nl,
    write("- - | 0 1 2 3 4 5 6 | "), nl,
    write("0 0 - - | X X X | - - "), nl,
    write("1 0 - - | X - X | - - "), nl,
    write("2 0 | X X X X X X X | "), nl,
    write("3 0 | X - X 0 - X X | "), nl,
    write("4 0 | X X X X X X X | "), nl,
    write("5 0 - - | X - X | - - "), nl,
    write("6 0 - - | X - X | - - "), nl,
    write("Moves to solution: "), write(X).
peg(full) :-
    fast_steps(full, [2,3,4,12,13,14,20,21,22,23,24,25,26,30,31,32,34,35,36,40,41,42,43,44,45,46,52,53,54,62,63,64], [], X),
    write("Start Board: (X = pegs, - = empty, 0 = goal)"), nl,
    write("- - | 0 1 2 3 4 5 6 | "), nl,
    write("0 0 - - | X X X | - - "), nl,
    write("1 0 - - | X X X | - - "), nl,
    write("2 0 | X X X X X X X | "), nl,
    write("3 0 | X X X 0 X X X | "), nl,
    write("4 0 | X X X X X X X | "), nl,
    write("5 0 - - | X X X | - - "), nl,
    write("6 0 - - | X X X | - - "), nl,
    write("Moves to solution: "), write(X).

% Remove predicate 
remove(X, [X|T], T).
remove(X, [H|T], [H|Tt]) :- remove(X, T, Tt).

% Non member predicate
nonmember(Arg,[Arg|_]) :-
        !,
        fail.
nonmember(Arg,[_|Tail]) :-
        !,
        nonmember(Arg,Tail).
nonmember(_,[]).

% Check to see if given piece is on board
onboard(N) :- 2 =< N, N =< 4.
onboard(N) :- 12 =< N, N =< 14.
onboard(N) :- 20 =< N, N =< 26.
onboard(N) :- 30 =< N, N =< 36.
onboard(N) :- 40 =< N, N =< 46.
onboard(N) :- 52 =< N, N =< 54.
onboard(N) :- 62 =< N, N =< 64.

% Does the jump(move)
jump(Start, Jumped, End) :-
    Jumped is Start + 1,
    End is Start + 2,
    onboard(Start),
    onboard(Jumped),
    onboard(End).
jump(Start, Jumped, End) :-
    Jumped is Start - 1,
    End is Start - 2,
    onboard(Start),
    onboard(Jumped),
    onboard(End).
jump(Start, Jumped, End) :-
    Jumped is Start + 10,
    End is Start + 20,
    onboard(Start),
    onboard(Jumped),
    onboard(End).
jump(Start, Jumped, End) :-
    Jumped is Start - 10,
    End is Start - 20,
    onboard(Start),
    onboard(Jumped),
    onboard(End).

% Applies the move
solitare_move(SB, (Start, End), [End|SB2]) :-
    remove(Start, SB, SB1),
    jump(Start, Jumped, End),
    remove(Jumped, SB1, SB2),
    nonmember(End, SB2).

% Finds all the moves to provide a solution
% Only being used for simple problems, crossbow and longbow
solitare_steps(SB, [Mv|Moves], GB) :-
    solitare_move(SB, Mv, SB1),
    solitare_steps(SB1, Moves, GB).
solitare_steps(GB, [], GB).

% Finds all moves to provide a solution for harder problems
% Being used for halfdead, notqdead and full
fast_steps(G, B, _, []) :- final_board(G, B).
fast_steps(G, B, Hist, [Mv|Moves]) :-
    solitare_move(B, Mv, NewBoard),
    independent(Mv, Hist),
    findall((P, W),(member(P, [simple, symm, rsymm]), %list of pagoda functions
                wgt(P, NewBoard, W)), Wgts),
    check_wgts(G, Wgts),
    fast_steps(G, NewBoard, [Mv|Hist], Moves).

% Goal board positions for given game
final_board(full, [33]).
final_board(notqdead, [33]).
final_board(halfdead, [33]).
    

% Independence checking here :c
independent(_, []).
independent(Mv, [H|_]) :-
    overlap(Mv, H), !.
independent(Mv, [H|T]) :-
    lexorder(Mv, H),
    independent(Mv, T).

% Check for any overlap 
overlap((Start1, End1), (Start2, End2)) :-
    jump(Start1, Jumped1, End1),
    jump(Start2, Jumped2, End2),
    (Start1 == Start2;
     Start1 == Jumped2;
     Start1 == End2;
     Jumped1 == Start2;
     Jumped1 == Jumped2;
     Jumped1 == End2;
     End1 == Start2;
     End1 == Jumped2;
     End1 == End2).


% This should be fine?
lexorder((A, B), (C, D)) :-
    jump(A, E, B),
    jump(C, F, D),
    X is A + B + E,
    Y is C + D + F,
    X =< Y.

% Pagoda functions

% Smiple
pagoda(simple, 13, 1).
pagoda(simple, 31, 1).
pagoda(simple, 33, 1).
pagoda(simple, 35, 1).
pagoda(simple, 43, 1).

% Symm
pagoda(symm, 13, 1).
pagoda(symm, 20, -1).
pagoda(symm, 21, 1).
pagoda(symm, 23, 1).
pagoda(symm, 25, 1).
pagoda(symm, 26, -1).
pagoda(symm, 31, 2).
pagoda(symm, 33, 2).
pagoda(symm, 35, 2).
pagoda(symm, 40, -1).
pagoda(symm, 41, 1).
pagoda(symm, 43, 1).
pagoda(symm, 45, 1).
pagoda(symm, 46, -1).
pagoda(symm, 53, 1).

% Rsymm
pagoda(rsymm, 2, -1).
pagoda(rsymm, 4, -1).
pagoda(rsymm, 12, 1).
pagoda(rsymm, 14, 1).
pagoda(rsymm, 31, 1).
pagoda(rsymm, 32, 1).
pagoda(rsymm, 34, 1).
pagoda(rsymm, 36, 1).
pagoda(rsymm, 52, 1).
pagoda(rsymm, 54, 1).
pagoda(rsymm, 62, -1).
pagoda(rsymm, 64, -1).


% Weighting functions from lectures
wgt(_,[], 0).
wgt(P,[Pos|Rest], Wgt) :-
    (pagoda(P,Pos,PWgt) ; PWgt = 0), !,
    wgt(P,Rest,WgtRest),
    Wgt is WgtRest + PWgt.

check_wgts(_, []).
check_wgts(G, [(P, WgtP)|Rest]) :-
    goal_wgt(G, P, WgtGoal),
    WgtP >= WgtGoal,
    check_wgts(G, Rest).

% End goal weights 
goal_wgt(full, simple, 1).
goal_wgt(full, symm, 2).
goal_wgt(full, rsymm, 0).
goal_wgt(notqdead, simple, 1).
goal_wgt(notqdead, symm, 2).
goal_wgt(notqdead, rsymm, 0).
goal_wgt(halfdead, simple, 1).
goal_wgt(halfdead, symm, 2).
goal_wgt(halfdead, rsymm, 0).
