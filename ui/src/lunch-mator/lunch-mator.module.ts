import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {LunchMatorRootComponent} from 'src/lunch-mator/lunch-mator-root.component';

@NgModule({
    declarations: [
        LunchMatorRootComponent,
    ],
    imports: [
        BrowserModule
    ],
    providers: [
    ],
    bootstrap: [
        LunchMatorRootComponent,
    ]
})
export class LunchMatorModule {
}
