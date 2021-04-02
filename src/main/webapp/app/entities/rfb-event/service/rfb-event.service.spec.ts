import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IRfbEvent, RfbEvent } from '../rfb-event.model';

import { RfbEventService } from './rfb-event.service';

describe('Service Tests', () => {
  describe('RfbEvent Service', () => {
    let service: RfbEventService;
    let httpMock: HttpTestingController;
    let elemDefault: IRfbEvent;
    let expectedResult: IRfbEvent | IRfbEvent[] | boolean | null;
    let currentDate: dayjs.Dayjs;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(RfbEventService);
      httpMock = TestBed.inject(HttpTestingController);
      currentDate = dayjs();

      elemDefault = {
        id: 0,
        eventDate: currentDate,
        eventCode: 'AAAAAAA',
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            eventDate: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a RfbEvent', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            eventDate: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            eventDate: currentDate,
          },
          returnedFromService
        );

        service.create(new RfbEvent()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a RfbEvent', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            eventDate: currentDate.format(DATE_FORMAT),
            eventCode: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            eventDate: currentDate,
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a RfbEvent', () => {
        const patchObject = Object.assign(
          {
            eventCode: 'BBBBBB',
          },
          new RfbEvent()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign(
          {
            eventDate: currentDate,
          },
          returnedFromService
        );

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of RfbEvent', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            eventDate: currentDate.format(DATE_FORMAT),
            eventCode: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            eventDate: currentDate,
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a RfbEvent', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addRfbEventToCollectionIfMissing', () => {
        it('should add a RfbEvent to an empty array', () => {
          const rfbEvent: IRfbEvent = { id: 123 };
          expectedResult = service.addRfbEventToCollectionIfMissing([], rfbEvent);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(rfbEvent);
        });

        it('should not add a RfbEvent to an array that contains it', () => {
          const rfbEvent: IRfbEvent = { id: 123 };
          const rfbEventCollection: IRfbEvent[] = [
            {
              ...rfbEvent,
            },
            { id: 456 },
          ];
          expectedResult = service.addRfbEventToCollectionIfMissing(rfbEventCollection, rfbEvent);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a RfbEvent to an array that doesn't contain it", () => {
          const rfbEvent: IRfbEvent = { id: 123 };
          const rfbEventCollection: IRfbEvent[] = [{ id: 456 }];
          expectedResult = service.addRfbEventToCollectionIfMissing(rfbEventCollection, rfbEvent);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(rfbEvent);
        });

        it('should add only unique RfbEvent to an array', () => {
          const rfbEventArray: IRfbEvent[] = [{ id: 123 }, { id: 456 }, { id: 82400 }];
          const rfbEventCollection: IRfbEvent[] = [{ id: 123 }];
          expectedResult = service.addRfbEventToCollectionIfMissing(rfbEventCollection, ...rfbEventArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const rfbEvent: IRfbEvent = { id: 123 };
          const rfbEvent2: IRfbEvent = { id: 456 };
          expectedResult = service.addRfbEventToCollectionIfMissing([], rfbEvent, rfbEvent2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(rfbEvent);
          expect(expectedResult).toContain(rfbEvent2);
        });

        it('should accept null and undefined values', () => {
          const rfbEvent: IRfbEvent = { id: 123 };
          expectedResult = service.addRfbEventToCollectionIfMissing([], null, rfbEvent, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(rfbEvent);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
