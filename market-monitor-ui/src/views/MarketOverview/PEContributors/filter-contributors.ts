export function filterContributors(
    contributors: PriceToEarningContributor[],
    industries: string[],
    sectors: string[]
): PriceToEarningContributor[] {
    return contributors.filter(({industry, sector}) => {
        return (industries.indexOf(industry) !== -1 || industries.length === 0)
            && (sectors.indexOf(sector) !== -1 || sectors.length === 0)
    });
}