import { useState } from 'react';
import { useVehicleList } from '../features/vehicles/hooks/useVehicles';
import VehicleCard from '../features/vehicles/components/VehicleCard';
import VehicleFilters from '../features/vehicles/components/VehicleFilters';
import Pagination from '../components/Pagination/Pagination';
import Spinner from '../components/Spinner/Spinner';
import ErrorDisplay from '../components/ErrorDisplay/ErrorDisplay';
import EmptyState from '../components/EmptyState/EmptyState';

function VehicleListPage() {
  const [filters, setFilters] = useState({ page: 0, size: 12 });
  const { data, isLoading, error } = useVehicleList(filters);

  const handleFilter = (newFilters) => {
    setFilters((prev) => ({ ...prev, ...newFilters, page: 0 }));
  };

  const handlePageChange = (page) => {
    setFilters((prev) => ({ ...prev, page: page - 1 }));
  };

  if (isLoading) {
    return (
      <div className="flex justify-center items-center py-20">
        <Spinner size="lg" />
      </div>
    );
  }

  if (error) {
    return <ErrorDisplay message={error.message} />;
  }

  const vehicles = data?.content || [];
  const totalPages = data?.totalPages || 0;

  return (
    <div>
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-text-primary mb-4">Vehicle Inventory</h1>
        <VehicleFilters onFilter={handleFilter} />
      </div>

      {vehicles.length === 0 ? (
        <EmptyState message="No vehicles found matching your criteria." />
      ) : (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 mt-6">
            {vehicles.map((vehicle) => (
              <VehicleCard key={vehicle.id} vehicle={vehicle} />
            ))}
          </div>
          {totalPages > 1 && (
            <div className="mt-8">
              <Pagination
                currentPage={filters.page + 1}
                totalPages={totalPages}
                onPageChange={handlePageChange}
              />
            </div>
          )}
        </>
      )}
    </div>
  );
}

export default VehicleListPage;
