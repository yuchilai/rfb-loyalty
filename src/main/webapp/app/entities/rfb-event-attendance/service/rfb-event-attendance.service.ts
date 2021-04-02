import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IRfbEventAttendance, getRfbEventAttendanceIdentifier } from '../rfb-event-attendance.model';

export type EntityResponseType = HttpResponse<IRfbEventAttendance>;
export type EntityArrayResponseType = HttpResponse<IRfbEventAttendance[]>;

@Injectable({ providedIn: 'root' })
export class RfbEventAttendanceService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/rfb-event-attendances');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(rfbEventAttendance: IRfbEventAttendance): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(rfbEventAttendance);
    return this.http
      .post<IRfbEventAttendance>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(rfbEventAttendance: IRfbEventAttendance): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(rfbEventAttendance);
    return this.http
      .put<IRfbEventAttendance>(`${this.resourceUrl}/${getRfbEventAttendanceIdentifier(rfbEventAttendance) as number}`, copy, {
        observe: 'response',
      })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(rfbEventAttendance: IRfbEventAttendance): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(rfbEventAttendance);
    return this.http
      .patch<IRfbEventAttendance>(`${this.resourceUrl}/${getRfbEventAttendanceIdentifier(rfbEventAttendance) as number}`, copy, {
        observe: 'response',
      })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IRfbEventAttendance>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IRfbEventAttendance[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addRfbEventAttendanceToCollectionIfMissing(
    rfbEventAttendanceCollection: IRfbEventAttendance[],
    ...rfbEventAttendancesToCheck: (IRfbEventAttendance | null | undefined)[]
  ): IRfbEventAttendance[] {
    const rfbEventAttendances: IRfbEventAttendance[] = rfbEventAttendancesToCheck.filter(isPresent);
    if (rfbEventAttendances.length > 0) {
      const rfbEventAttendanceCollectionIdentifiers = rfbEventAttendanceCollection.map(
        rfbEventAttendanceItem => getRfbEventAttendanceIdentifier(rfbEventAttendanceItem)!
      );
      const rfbEventAttendancesToAdd = rfbEventAttendances.filter(rfbEventAttendanceItem => {
        const rfbEventAttendanceIdentifier = getRfbEventAttendanceIdentifier(rfbEventAttendanceItem);
        if (rfbEventAttendanceIdentifier == null || rfbEventAttendanceCollectionIdentifiers.includes(rfbEventAttendanceIdentifier)) {
          return false;
        }
        rfbEventAttendanceCollectionIdentifiers.push(rfbEventAttendanceIdentifier);
        return true;
      });
      return [...rfbEventAttendancesToAdd, ...rfbEventAttendanceCollection];
    }
    return rfbEventAttendanceCollection;
  }

  protected convertDateFromClient(rfbEventAttendance: IRfbEventAttendance): IRfbEventAttendance {
    return Object.assign({}, rfbEventAttendance, {
      attendanceDate: rfbEventAttendance.attendanceDate?.isValid() ? rfbEventAttendance.attendanceDate.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.attendanceDate = res.body.attendanceDate ? dayjs(res.body.attendanceDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((rfbEventAttendance: IRfbEventAttendance) => {
        rfbEventAttendance.attendanceDate = rfbEventAttendance.attendanceDate ? dayjs(rfbEventAttendance.attendanceDate) : undefined;
      });
    }
    return res;
  }
}
