package body Maximum
  with SPARK_Mode
is
   function Maximum (A : Vector) return Integer is
      Max    : Integer;
   begin
         Max := A (A'First);
      for Actual in A'Range loop
         pragma Loop_Invariant
           ((for all Index in A'First .. (Actual)
              => A (Index) <= Max or (Index = Actual and A(Index) >= Max )) and then (for some i in A'Range => A(i) = Max));
         pragma Loop_Variant (Increases => Actual);
         if A (Actual) >= Max then
            Max := A (Actual);
         end if;
      end loop;
      return Max;
   end Maximum;

end Maximum;
