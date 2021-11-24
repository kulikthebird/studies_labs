pragma SPARK_Mode(on);
procedure SWAP(X : in out Integer; Y : in out Integer)
  with
 Pre => (Integer'Last/2 > X/2 + Y/2) and (Integer'First/2 < X/2 + Y/2),
 Post => (X'Old = Y) and (Y'Old = X);
