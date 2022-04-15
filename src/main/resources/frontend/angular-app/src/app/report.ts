import { CostCentre } from "./costCentre";

export class Report {

    costCentres: CostCentre[] | undefined;

    friendlyName: string | undefined;
    year: number | undefined;
    month: string | undefined;
    income: number | undefined;
    expenditure: number | undefined;
    win: number | undefined;
    savingRate: number | undefined;
    investment: number | undefined;
    
}