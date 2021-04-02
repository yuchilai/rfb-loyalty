import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IRfbEventAttendance, RfbEventAttendance } from '../rfb-event-attendance.model';

import { RfbEventAttendanceService } from './rfb-event-attendance.service';

describe('Service Tests', () => {
  describe('RfbEventAttendance Service', () => {
    let service: RfbEventAttendanceService;
    let httpMock: HttpTestingController;
    let elemDefault: IRfbEventAttendance;
    let expectedResult: IRfbEventAttendance | IRfbEventAttendance[] | boolean | null;
    let currentDate: dayjs.Dayjs;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(RfbEventAttendanceService);
      httpMock = TestBed.inject(HttpTestingController);
      currentDate = dayjs();

      elemDefault = {
        id: 0,
        attendanceDate: currentDate,
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            attendanceDate: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a RfbEventAttendance', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            attendanceDate: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            attendanceDate: currentDate,
          },
          returnedFromService
        );

        service.create(new RfbEventAttendance()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a RfbEventAttendance', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            attendanceDate: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            attendanceDate: currentDate,
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a RfbEventAttendance', () => {
        const patchObject = Object.assign({}, new RfbEventAttendance());

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign(
          {
            attendanceDate: currentDate,
          },
          returnedFromService
        );

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of RfbEventAttendance', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            attendanceDate: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            attendanceDate: currentDate,
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a RfbEventAttendance', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addRfbEventAttendanceToCollectionIfMissing', () => {
        it('should add a RfbEventAttendance to an empty array', () => {
          const rfbEventAttendance: IRfbEventAttendance = { id: 123 };
          expectedResult = service.addRfbEventAttendanceToCollectionIfMissing([], rfbEventAttendance);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(rfbEventAttendance);
        });

        it('should not add a RfbEventAttendance to an array that contains it', () => {
          const rfbEventAttendance: IRfbEventAttendance = { id: 123 };
          const rfbEventAttendanceCollection: IRfbEventAttendance[] = [
            {
              ...rfbEventAttendance,
            },
            { id: 456 },
          ];
          expectedResult = service.addRfbEventAttendanceToCollectionIfMissing(rfbEventAttendanceCollection, rfbEventAttendance);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a RfbEventAttendance to an array that doesn't contain it", () => {
          const rfbEventAttendance: IRfbEventAttendance = { id: 123 };
          const rfbEventAttendanceCollection: IRfbEventAttendance[] = [{ id: 456 }];
          expectedResult = service.addRfbEventAttendanceToCollectionIfMissing(rfbEventAttendanceCollection, rfbEventAttendance);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(rfbEventAttendance);
        });

        it('should add only unique RfbEventAttendance to an array', () => {
          const rfbEventAttendanceArray: IRfbEventAttendance[] = [{ id: 123 }, { id: 456 }, { id: 5056 }];
          const rfbEventAttendanceCollection: IRfbEventAttendance[] = [{ id: 123 }];
          expectedResult = service.addRfbEventAttendanceToCollectionIfMissing(rfbEventAttendanceCollection, ...rfbEventAttendanceArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const rfbEventAttendance: IRfbEventAttendance = { id: 123 };
          const rfbEventAttendance2: IRfbEventAttendance = { id: 456 };
          expectedResult = service.addRfbEventAttendanceToCollectionIfMissing([], rfbEventAttendance, rfbEventAttendance2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(rfbEventAttendance);
          expect(expectedResult).toContain(rfbEventAttendance2);
        });

        it('should accept null and undefined values', () => {
          const rfbEventAttendance: IRfbEventAttendance = { id: 123 };
          expectedResult = service.addRfbEventAttendanceToCollectionIfMissing([], null, rfbEventAttendance, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(rfbEventAttendance);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
