import * as dayjs from 'dayjs';
import { IRfbEventAttendance } from 'app/entities/rfb-event-attendance/rfb-event-attendance.model';
import { IRfbLocation } from 'app/entities/rfb-location/rfb-location.model';

export interface IRfbEvent {
  id?: number;
  eventDate?: dayjs.Dayjs | null;
  eventCode?: string | null;
  rfbEventAttendances?: IRfbEventAttendance[] | null;
  rfbLocation?: IRfbLocation | null;
}

export class RfbEvent implements IRfbEvent {
  constructor(
    public id?: number,
    public eventDate?: dayjs.Dayjs | null,
    public eventCode?: string | null,
    public rfbEventAttendances?: IRfbEventAttendance[] | null,
    public rfbLocation?: IRfbLocation | null
  ) {}
}

export function getRfbEventIdentifier(rfbEvent: IRfbEvent): number | undefined {
  return rfbEvent.id;
}
