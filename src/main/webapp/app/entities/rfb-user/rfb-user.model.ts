import { IRfbLocation } from 'app/entities/rfb-location/rfb-location.model';
import { IRfbEventAttendance } from 'app/entities/rfb-event-attendance/rfb-event-attendance.model';

export interface IRfbUser {
  id?: number;
  username?: string | null;
  homeLocation?: IRfbLocation | null;
  rfbEventAttendances?: IRfbEventAttendance[] | null;
}

export class RfbUser implements IRfbUser {
  constructor(
    public id?: number,
    public username?: string | null,
    public homeLocation?: IRfbLocation | null,
    public rfbEventAttendances?: IRfbEventAttendance[] | null
  ) {}
}

export function getRfbUserIdentifier(rfbUser: IRfbUser): number | undefined {
  return rfbUser.id;
}
