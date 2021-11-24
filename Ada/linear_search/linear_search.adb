package body Linear_Search
  with SPARK_Mode
is

   function Search (A : Ar; I : Integer) return T is
   begin
      for Actual in A'Range loop
         pragma Loop_Invariant
           (( if Actual <= Ar'Last and Actual >= Ar'First then(
            if Actual > A'First then (for all Index in A'First .. (Actual - 1)
              => A (Index) /= I))));
         pragma Loop_Variant (Decreases => A'Length - Actual);
         if A (Actual) = I then
            return Actual;
         end if;
      end loop;

      return 0;
   end Search;

end Linear_Search;
