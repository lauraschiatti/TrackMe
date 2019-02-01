import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {Track4runComponent} from './track4run.component';

describe('Track4runComponent', () => {
    let component: Track4runComponent;
    let fixture: ComponentFixture<Track4runComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            declarations: [Track4runComponent]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(Track4runComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
