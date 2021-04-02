import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IRfbLocation, RfbLocation } from '../rfb-location.model';

import { RfbLocationService } from './rfb-location.service';

describe('Service Tests', () => {
  describe('RfbLocation Service', () => {
    let service: RfbLocationService;
    let httpMock: HttpTestingController;
    let elemDefault: IRfbLocation;
    let expectedResult: IRfbLocation | IRfbLocation[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(RfbLocationService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        locationName: 'AAAAAAA',
        runDayOfWeek: 0,
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign({}, elemDefault);

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a RfbLocation', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new RfbLocation()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a RfbLocation', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            locationName: 'BBBBBB',
            runDayOfWeek: 1,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a RfbLocation', () => {
        const patchObject = Object.assign({}, new RfbLocation());

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of RfbLocation', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            locationName: 'BBBBBB',
            runDayOfWeek: 1,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a RfbLocation', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addRfbLocationToCollectionIfMissing', () => {
        it('should add a RfbLocation to an empty array', () => {
          const rfbLocation: IRfbLocation = { id: 123 };
          expectedResult = service.addRfbLocationToCollectionIfMissing([], rfbLocation);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(rfbLocation);
        });

        it('should not add a RfbLocation to an array that contains it', () => {
          const rfbLocation: IRfbLocation = { id: 123 };
          const rfbLocationCollection: IRfbLocation[] = [
            {
              ...rfbLocation,
            },
            { id: 456 },
          ];
          expectedResult = service.addRfbLocationToCollectionIfMissing(rfbLocationCollection, rfbLocation);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a RfbLocation to an array that doesn't contain it", () => {
          const rfbLocation: IRfbLocation = { id: 123 };
          const rfbLocationCollection: IRfbLocation[] = [{ id: 456 }];
          expectedResult = service.addRfbLocationToCollectionIfMissing(rfbLocationCollection, rfbLocation);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(rfbLocation);
        });

        it('should add only unique RfbLocation to an array', () => {
          const rfbLocationArray: IRfbLocation[] = [{ id: 123 }, { id: 456 }, { id: 85940 }];
          const rfbLocationCollection: IRfbLocation[] = [{ id: 123 }];
          expectedResult = service.addRfbLocationToCollectionIfMissing(rfbLocationCollection, ...rfbLocationArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const rfbLocation: IRfbLocation = { id: 123 };
          const rfbLocation2: IRfbLocation = { id: 456 };
          expectedResult = service.addRfbLocationToCollectionIfMissing([], rfbLocation, rfbLocation2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(rfbLocation);
          expect(expectedResult).toContain(rfbLocation2);
        });

        it('should accept null and undefined values', () => {
          const rfbLocation: IRfbLocation = { id: 123 };
          expectedResult = service.addRfbLocationToCollectionIfMissing([], null, rfbLocation, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(rfbLocation);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
