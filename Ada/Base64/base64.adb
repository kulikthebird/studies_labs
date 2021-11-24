pragma SPARK_Mode(on);

with Interfaces; use Interfaces;
package body Base64 is

   function Encode (D : in Data_Type) return Encoded_Data_Type
   is
      X : Positive;
      Y : Positive;
      Data : Encoded_Data_Type(1 .. 4*(D'Length/3)) := (others => 0);
   begin

      X := D'First;
      Y := Data'First;
      if D'Length = 0 then
         return Data;
      end if;
      while Y >= Data'First and Y + 3 <= Data'Last and X >= D'First and X + 2 <= D'Last loop
         pragma Loop_Invariant
           ( Y >= Data'First and Y + 3 <= Data'Last and X >= D'First and X + 2 <= D'Last);
         Data (Y+0) := Sextet(Shift_Right( Unsigned_8(D(X+0)), 2));
         Data (Y+1) := Sextet(Shift_Left( Unsigned_8(D(X+0)), 4) rem 64) + Sextet(Shift_Right( Unsigned_8(D(X+1)), 4) rem 64);
         Data (Y+2) := Sextet(Shift_Left( Unsigned_8(D(X+1)), 2) rem 64) + Sextet(Shift_Right( Unsigned_8(D(X+2)), 6) rem 64);
         Data (Y+3) := Sextet(D(X+2) rem 64);
         X := X+3;
         Y := Y+4;
      end loop;
      return Data;
   end;

   function Decode (E : in Encoded_Data_Type) return Data_Type
   is
      X : Positive;
      Y : Positive;
      Data : Data_Type(1 .. (3*(E'Length/4))) := (others => 0);
   begin

      X := Data'First;
      Y := E'First;
      if E'Length = 0 then
         return Data;
      end if;
      while X >= Data'First and X + 2 <= Data'Last and Y >= E'First and Y + 3 <= E'Last loop
         pragma Loop_Invariant
           (X >= Data'First and Y >= E'First and X + 2 <= Data'Last and Y >= E'First and Y + 3 <= E'Last);
         Data(X+0) := Octet(Shift_Left( Unsigned_8(E(Y+0)), 2) + Shift_Right(Unsigned_8(E(Y+1)), 4));
         Data(X+1) := Octet(Shift_Left( Unsigned_8(E(Y+1)), 4) + Shift_Right(Unsigned_8(E(Y+2)), 2));
         Data(X+2) := Octet(Shift_Left( Unsigned_8(E(Y+2)), 6) + Unsigned_8(E(Y+3)));
         X := X+3;
         Y := Y+4;
      end loop;
   return Data;
end;

end Base64;
