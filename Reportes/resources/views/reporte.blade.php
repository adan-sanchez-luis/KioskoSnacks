<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <link href="{{ public_path('/css/reporte.css') }}" rel="stylesheet" type="text/css">
    <title>Sales report</title>
</head>

<body>
    <div class="logo">
        <div class="item"><img src="{{ public_path('/imagenes/ebrigge_logo.jpg') }}"></div>
        <div class="item2">
            <h2>Sales report</h2>
            <h3>{{ $days['start'] }} to {{ $days['end'] }}</h3>
        </div>
    </div>
    <div class="data" style="margin-top: 30px;">
        <table id="customers">
            <thead>
                <tr>
                    <th rowspan="2">Date</th>
                    <th colspan="3">Sales</th>
                </tr>
                <tr>
                    <th>Product</th>
                    <th>Quantity</th>
                    <th>sale</th>
                </tr>
            </thead>

            <tbody>
                @php
                    $totalQuantity = 0;
                    $totalSale = 0;
                @endphp
                @foreach ($reporte as $reporteData)
                    @php
                        $reporteDia = $reporteData['reporteDia'];
                        $detallesDia = $reporteData['detallesDia'];
                    @endphp
                    <tr>
                        <td rowspan="{{ count($detallesDia) }}" style="text-align: center;">{{ $reporteDia->day }}</td>
                        @php
                            $totalDia = 0;
                            $totalVentaDia = 0;
                        @endphp
                        @foreach ($detallesDia as $index => $detalleDia)
                            <td>{{ $detalleDia->name }}</td>
                            <td style="text-align: center;">{{ $detalleDia->total_quantity }}</td>
                            <td style="text-align: center;">${{ $detalleDia->total_price }}</td>
                    </tr>
                    @php
                        $totalDia += $detalleDia->total_quantity;
                        $totalVentaDia += $detalleDia->total_price;
                        $totalQuantity += $detalleDia->total_quantity;
                        $totalSale += $detalleDia->total_price;
                    @endphp
                    @if ($index > 0)
                        </tr>
                    @endif
                @endforeach
                <tr style="background-color: #dbdbdb;">
                    <td colspan="2" style="text-align: right;">Total:</td>
                    <td style="text-align: center;">{{ $totalDia }}</td>
                    <td style="text-align: center;">${{ $totalVentaDia }}</td>
                </tr>
                @endforeach
            </tbody>
            <tfoot>
                <tr>
                    <td colspan="2" style="text-align: right;">Total:</td>
                    <td style="text-align: center;">{{ $totalQuantity }}</td>
                    <td style="text-align: center;">${{ $totalSale }}</td>
                </tr>
            </tfoot>
        </table>
    </div>
</body>

</html>
