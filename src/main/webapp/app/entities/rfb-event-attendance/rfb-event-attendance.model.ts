import * as dayjs from 'dayjs';
import { IRfbEvent } from 'app/entities/rfb-event/rfb-event.model';
import { IRfbUser } from 'app/entities/rfb-user/rfb-user.model';

export interface IRfbEventAttendance {
  id?: number;
  attendanceDate?: dayjs.Dayjs | null;
  rfbEvent?: IRfbEvent | null;
  rfbUser?: IRfbUser | null;
}

export class RfbEventAttendance implements IRfbEventAttendance {
  constructor(
    public id?: number,
    public attendanceDate?: dayjs.Dayjs | null,
    public rfbEvent?: IRfbEvent | null,
    public rfbUser?: IRfbUser | null
  ) {}
}

export function getRfbEventAttendanceIdentifier(rfbEventAttendance: IRfbEventAttendance): number | undefined {
  return rfbEventAttendance.id;
}
