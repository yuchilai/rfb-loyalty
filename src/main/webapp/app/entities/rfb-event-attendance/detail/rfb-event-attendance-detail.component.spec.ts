import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { RfbEventAttendanceDetailComponent } from './rfb-event-attendance-detail.component';

describe('Component Tests', () => {
  describe('RfbEventAttendance Management Detail Component', () => {
    let comp: RfbEventAttendanceDetailComponent;
    let fixture: ComponentFixture<RfbEventAttendanceDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [RfbEventAttendanceDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ rfbEventAttendance: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(RfbEventAttendanceDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(RfbEventAttendanceDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load rfbEventAttendance on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.rfbEventAttendance).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
