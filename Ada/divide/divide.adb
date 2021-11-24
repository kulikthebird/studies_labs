pragma SPARK_Mode(on);

procedure DIVIDE(X : in Natural; Y : in Positive;
                 Q : out Natural; R : out Natural)
is
begin
   Q := 0;
   R := X;
   while R >= Y loop
      pragma Loop_Invariant ( Q * Y + R = X );

      R := R - Y;
      Q := Q + 1;
   end loop;
end DIVIDE;
