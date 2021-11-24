package body Railway
with SPARK_Mode
is
   procedure Open_Route (Route: in Route_Type; Success: out Boolean)
   is
   begin
      Success := False;
      if Route = Route_Left_Middle then
         if Segment_State.Middle = Free and
           Segment_State.Left = Occupied_Standing then
            Signal_state.Left_Middle := Green;
            Segment_State.Left := Occupied_Moving_Right;
            Segment_State.Middle := Reserved_Moving_From_Left;
            Success := True;
         end if;
      elsif Route = Route_Middle_Right then
         if Segment_State.Right = Free and
           Segment_State.Middle = Occupied_Standing then
            Signal_state.Middle_Right := Green;
            Segment_State.Middle := Occupied_Moving_Right;
            Segment_State.Right := Reserved_Moving_From_Left;
            Success := True;
         end if;
      elsif Route = Route_Right_Middle then
         if Segment_State.Middle = Free and
           Segment_State.Right = Occupied_Standing then
            Signal_state.Right_Middle := Green;
            Segment_State.Right := Occupied_Moving_Left;
            Segment_State.Middle := Reserved_Moving_From_Right;
            Success := True;
         end if;
      elsif Route = Route_Middle_Left then
         if Segment_State.Left = Free and
           Segment_State.Middle = Occupied_Standing then
            Signal_state.Middle_Left := Green;
            Segment_State.Middle := Occupied_Moving_Left;
            Segment_State.Left := Reserved_Moving_From_Right;
            Success := True;
         end if;
      elsif Route = Route_Enter_Left then
         if Segment_State.Left = Free then
            Segment_State.Left := Reserved_Moving_From_Left;
            Success := True;
         end if;
      elsif Route = Route_Leave_Right then
         if Segment_State.Right = Occupied_Standing then
            Segment_State.Right := Occupied_Moving_Right;
            Success := True;
         end if;
      elsif Route = Route_Enter_Right then
         if Segment_State.Right = Free then
            Segment_State.Right := Reserved_Moving_From_Right;
            Success := True;
         end if;
      elsif Route = Route_Leave_Left then
         if Segment_State.Left = Occupied_Standing then
            Segment_State.Left := Occupied_Moving_Left;
            Success := True;
         end if;
      end if;
   end Open_Route;


   procedure Move_Train (Route: in Route_Type; Success: out Boolean)
   is
   begin
      Success := False;
      if Route = Route_Left_Middle then
         if Segment_State.Left = Occupied_Moving_Right and
           Segment_State.Middle = Reserved_Moving_From_Left then
            Signal_state.Left_Middle := Red;
            Segment_State.Left := Free;
            Segment_State.Middle := Occupied_Standing;
            Success := True;
         end if;
      elsif Route = Route_Middle_Right then
         if Segment_State.Middle = Occupied_Moving_Right and
           Segment_State.Right = Reserved_Moving_From_Left then
            Signal_state.Middle_Right := Red;
            Segment_State.Middle := Free;
            Segment_State.Right := Occupied_Standing;
            Success := True;
         end if;
      elsif Route = Route_Right_Middle then
         if Segment_State.Right = Occupied_Moving_Left and
           Segment_State.Middle = Reserved_Moving_From_Right then
            Signal_state.Right_Middle := Red;
            Segment_State.Right := Free;
            Segment_State.Middle := Occupied_Standing;
            Success := True;
         end if;
      elsif Route = Route_Middle_Left then
         if Segment_State.Middle = Occupied_Moving_Left and
           Segment_State.Left = Reserved_Moving_From_Right then
            Signal_state.Middle_Left := Red;
            Segment_State.Middle := Free;
            Segment_State.Left := Occupied_Standing;
            Success := True;
         end if;
      elsif Route = Route_Enter_Left then
         if Segment_State.Left = Reserved_Moving_From_Left then
            Segment_State.Left := Occupied_Standing;
            Success := True;
         end if;
      elsif Route = Route_Leave_Right then
         if Segment_State.Right = Occupied_Moving_Right then
            Segment_State.Right := Free;
            Success := True;
         end if;
      elsif Route = Route_Enter_Right then
         if Segment_State.Right = Reserved_Moving_From_Right then
            Segment_State.Right := Occupied_Standing;
            Success := True;
         end if;
      elsif Route = Route_Leave_Left then
         if Segment_State.Left = Occupied_Moving_Left then
            Segment_State.Left := Free;
            Success := True;
         end if;
      end if;
   end Move_Train;


end Railway;
