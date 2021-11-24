with Base64;
with Ada.Text_IO;

procedure Main2 is
   wstep : Base64.Data_Type(1 .. 3) := (others => 0);
   wynik : Base64.Encoded_Data_Type(1 .. 4);
begin

   for A in 0 .. 256*256*256 loop
      wstep(1) := Base64.Octet(A mod 256);
      wstep(2) := Base64.Octet((A/256) mod 256);
      wstep(3) := Base64.Octet((A/(256*256)) mod 256);
      if Base64.Decode(Base64.Encode(wstep)) /= wstep then
         Ada.Text_IO.Put_Line("Lipa\n");
      end if;
   end loop;
end Main2;
