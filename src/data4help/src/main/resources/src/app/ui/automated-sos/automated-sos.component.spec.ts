import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {AutomatedSOSComponent} from './automated-sos.component';

describe('AutomatedSOSComponent', () => {
    let component: AutomatedSOSComponent;
    let fixture: ComponentFixture<AutomatedSOSComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            declarations: [AutomatedSOSComponent]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(AutomatedSOSComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
