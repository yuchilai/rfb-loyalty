import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { RfbEventDetailComponent } from './rfb-event-detail.component';

describe('Component Tests', () => {
  describe('RfbEvent Management Detail Component', () => {
    let comp: RfbEventDetailComponent;
    let fixture: ComponentFixture<RfbEventDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [RfbEventDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ rfbEvent: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(RfbEventDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(RfbEventDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load rfbEvent on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.rfbEvent).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
