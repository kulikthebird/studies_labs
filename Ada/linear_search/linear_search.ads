package Linear_Search
  with SPARK_Mode
is

   type T is range 0 .. 10;
   subtype U is T range 1 .. 10;

   type Ar is array (U) of Integer;

   function Search (A : Ar; I : Integer) return T with
   -- If I exists in A, then Search'Result indicates its position
     Post => (if Search'Result in A'Range then A (Search'Result) = I
                  else (for all Index in A'Range => A (Index) /= I));

end Linear_Search;
