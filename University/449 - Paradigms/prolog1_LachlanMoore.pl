% Lachlan Moore, 30030228
% Fall 2019, CPSC 449
%
%%(Y + 1) + X = (Z + 1) iff X + Y = Z
sum(z,X,X).
sum(s(Y),X,s(Z)) :- sum(Y,X,Z).

%(x:xs) ++ ys == (z:zs) iff x == z and (xs ++ ys == zs)
concat([],Y,Y).
concat([H|Tone],Y,[H|Ttwo]) :- concat(Tone,Y,Ttwo).

% Problem 1.
% a) 
append([], X, X).                                   
append([X|Y], Z, [X|W]) :- append(Y, Z, W).

% b)
appLast(Hs, T, L) :- concat(Hs, [T], L).

reverse([], []).
reverse([H|T],Y) :- reverse(T, K), appLast(K, H, Y).

% c) Do you want to completely flatten the list?
flatten(List, FlatList) :-
	flatten(List, [], FlatList).
 
flatten(Var, T, [Var|T]) :-
	var(Var), !.
flatten([], T, T) :- !.
flatten([H|T], TailList, List) :- !,
	flatten(H, FlatTail, List),
	flatten(T, TailList, FlatTail).
 
flatten(NonList, T, [NonList|T]).

% d)
member(X, [X|_]).
member(X, [_|Y]) :- member(X, Y).

% e)
remove(X, [X|T], T).
remove(X, [H|T], [H|Tt]) :- remove(X, T, Tt).

% Problem 2.
member2(X, [X|T]) :- member(X, [X]), member(X, T).
member2(X, [_|T]) :- member2(X, T).

% Problem 3.
substring(X, S) :- append(_, T, S), append(X, _, T), X \= [].

% Problem 4.
sublists([], []).
sublists([_|Xs], S) :-
    sublists(Xs, S).
sublists([X|Xs], [X|Ys]) :-
    helper_sublist(Xs, Ys).

helper_sublist(_, []).
helper_sublist([X|Xs], [X|Ys]) :-
    helper_sublist(Xs, Ys).

% Problem 5.
% From lecture notes
perm([],[]).
perm([H|T],Z):-
    perm(T,S),
    insert(H,S,Z).

insert(H,T,[H|T]).
insert(H,[X|T],[X|Z]):-
    insert(H,T,Z).

permutation(X,Y):-
    samelength(X,Y),
    perm(X,Y).

samelength([],[]).
samelength([_|X],[_|Y]):-
    samelength(X,Y).

% Problem 6.
% Just so it is easier to mark
% Grandparents: Marelene, Ron, Sue and Jess
% Parents: Lisa and Bill
% Uncle and Aunt: Lucy, Sarah and Sam
% Cousins: Oscar, Izzy and Annie
% Sister: Laura
% Me: Lachlan
daughter('Marlene', 'Ron', 'Lisa').
daughter('Sue', 'Jess', 'Lucy').
daughter('Sarah', 'Sam', 'Izzy').
daughter('Sarah', 'Sam', 'Annie').
daughter('Lisa', 'Bill', 'Laura').
son('Sue', 'Jess', 'Bill').
son('Sue', 'Jess', 'Sam').
son('Sarah', 'Sam', 'Oscar').
son('Lisa', 'Bill', 'Lachlan').

grandfather(X, W) :- son(_, Y, X), daughter(_, W, Y);
    son(Y, _, X), daughter(_, W, Y);
    son(_, Y, X), son(_, W, Y);
    son(Y, _, X), son(_, W, Y);
    daughter(_, Y, X), daughter(_, W, Y);
    daughter(Y, _, X), daughter(_, W, Y);
    daughter(_, Y, X), son(_, W, Y);
    daughter(Y, _, X), son(_, W, Y).

grandmother(X, W) :- son(_, Y, X), daughter(W, _, Y);
    son(Y, _, X), daughter(W, _, Y);
    son(_, Y, X), son(W, _, Y);
    son(Y, _, X), son(W, _, Y);
    daughter(_, Y, X), daughter(W, _, Y);
    daughter(Y, _, X), daughter(W, _, Y);
    daughter(_, Y, X), son(W, _, Y);
    daughter(Y, _, X), son(W, _, Y).

brother(X, W) :- daughter(Z, Y, X), son(Z, Y, W).
brother(X, W) :- son(Z, Y, X), son(Z, Y, W), X \= W.

sister(X, W) :- son(Z, Y, X), daughter(Z, Y, W).
sister(X, W) :- daughter(Z, Y, X), daughter(Z, Y, W), X \= W.

sibling(X, W) :- son(Z, Y, X), daughter(Z, Y, W).
sibling(X, W) :- daughter(Z, Y, X), daughter(Z, Y, W), X \= W.
sibling(X, W) :- son(Z, Y, X), son(Z, Y, W), X \= W.
sibling(X, W) :- daughter(Z, Y, X), son(Z, Y, W).

cousin(X, W) :- son(Z, Y, X), son(T, Q, W), (sibling(Z,T);sibling(Z,Q);sibling(Y,T);sibling(Y,Q));
    son(Z, Y, X), daughter(T, Q, W), (sibling(Z,T);sibling(Z,Q);sibling(Y,T);sibling(Y,Q));
    daughter(Z, Y, X), son(T, Q, W), (sibling(Z,T);sibling(Z,Q);sibling(Y,T);sibling(Y,Q));
    daughter(Z, Y, X), daughter(T, Q, W), (sibling(Z,T);sibling(Z,Q);sibling(Y,T);sibling(Y,Q)).

% Problem 7.

edge(a, b).
edge(a, c).
edge(a, d).
edge(e, a).
edge(c, a).

connected(X, Y) :- edge(X, Y).
connected(X, Y) :- edge(Y, X).

path(A, B) :- paths(A, B, _).

paths(A, B, Path) :-
       travel(A, B, [A], Q), 
       reverse(Q, Path).

travel(A, B, P, [B|P]) :- 
       connected(A, B).
travel(A, B, Visited, Path) :-
       connected(A, C),           
       C \== B,
       \+member(C, Visited),
       travel(C, B, [C|Visited], Path). 

% Problem 8.
% Nationality, Pet, Drink, Colour, Sport
h(_, _, _, _, _).

hamster_owner(Owner) :-
    houses(Hs),
    member(h(Owner, hamster, _, _, _), Hs).

orange_juice_drinker(Drinker) :-
    houses(Hs),
    member(h(Drinker, _, orange, _, _), Hs).

houses(Hs) :-
    length(Hs, 5),
    Hs = [h(irish, _, _, _, _)|_],
    adjacent(h(_, _, _, _, baseball), h(_, tiger, _, _, _), Hs),
    adjacent(h(_, horse, _, _, _), h(_, _, _, _, soccer), Hs),
    member(h(_, _, gin, _, squash), Hs),
    member(h(french, _, _, _, rugger), Hs),
    adjacent(h(irish, _, _, _, _), h(_, _, _, blue, _), Hs),
    member(h(english, _, _, red, _), Hs),
    member(h(spanish, dog, _, _, _), Hs),
    member(h(_, _, beer, green, _), Hs),
    member(h(scotish, _, whiskey, _, _), Hs),
    nextTo(h(_, _, _, white, _), h(_, _, _, green, _), Hs),
    member(h(_, snake, _, _, tennis), Hs),
    member(h(_, _, _, yellow, soccer), Hs),
    Hs = [_, _, h(_, _, wine, _, _), _, _],
    member(h(_, hamster, _, _, _), Hs),
    member(h(_, _, orange, _, _), Hs).

nextTo(A, B, Ls) :- append(_, [A,B|_], Ls).

adjacent(A, B, Ls) :- append(_, [A,B|_], Ls).
adjacent(A, B, Ls) :- append(_, [B,A|_], Ls).


% Problem 9.
josephus(N, M) :- form_list(1, N, L), find_winner(L, M, Win), write('The winner is '), write(Win).

form_list(A, B, []) :- A > B.
form_list(A, B, L) :- A =< B, TmpA is A + 1, form_list(TmpA, B, TmpL), append([A], TmpL, L).

find_winner([Win], _, Win).
find_winner(L, M, Win) :- cycle(L, M, [_|TmpL]), find_winner(TmpL, M, Win).

cycle([], _, []).
cycle([H|T], 0, [H|T]).
cycle([H|T], N, L) :- N > 0, append(T, [H], Tmp), N1 is N - 1, cycle(Tmp, N1, L).

